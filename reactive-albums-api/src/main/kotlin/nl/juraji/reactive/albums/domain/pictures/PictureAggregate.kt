package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.pictures.commands.*
import nl.juraji.reactive.albums.domain.pictures.events.*
import nl.juraji.reactive.albums.domain.tags.TagId
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.common.Assert
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.MetaData
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.nio.file.Path

@Aggregate
class PictureAggregate() {

    @AggregateIdentifier
    private lateinit var pictureId: PictureId
    private lateinit var directoryId: DirectoryId
    private lateinit var location: Path
    private lateinit var displayName: String
    private var tags: Set<TagId> = emptySet()
    private var duplicates: Set<PictureId> = emptySet()

    @CommandHandler
    constructor(cmd: CreatePictureCommand) : this() {
        val displayName = cmd.displayName ?: cmd.location.fileName.toString()
        val pictureType = PictureType.of(cmd.contentType)

        Validate.isNotNull(pictureType) { "Unsupported file type for $displayName" }

        AggregateLifecycle.apply(
                PictureCreatedEvent(
                        pictureId = cmd.pictureId,
                        displayName = displayName,
                        location = cmd.location,
                        pictureType = pictureType!!,
                        directoryId = cmd.directoryId
                ),
                MetaData.with("AUDIT", "New picture added: $displayName")
        )
    }

    @CommandHandler
    fun handle(cmd: MovePictureCommand): PictureId {
        Validate.isFalse(directoryId == cmd.targetDirectoryId) { "Target directory is the same as the current location" }

        val targetLocation: Path =
                if (cmd.targetLocation.fileName == location.fileName) cmd.targetLocation
                else cmd.targetLocation.resolve(location.fileName)

        AggregateLifecycle.apply(
                PictureMovedEvent(
                        pictureId = pictureId,
                        sourceDirectoryId = directoryId,
                        sourceLocation = location,
                        targetDirectoryId = cmd.targetDirectoryId,
                        targetLocation = targetLocation
                ),
                MetaData.with("AUDIT", "Picture moved from $location to ${cmd.targetLocation}")
        )

        return pictureId
    }

    @CommandHandler
    fun handle(cmd: LinkTagCommand) {
        Validate.isFalse(tags.contains(cmd.tagId)) { "Tag with id ${cmd.tagId} is already present on $displayName" }

        AggregateLifecycle.apply(
                TagLinkedEvent(
                        pictureId = this.pictureId,
                        tagId = cmd.tagId,
                ),
                MetaData.with("AUDIT", "Tag (${cmd.tagId}) added")
        )
    }

    @CommandHandler
    fun handle(cmd: UnlinkTagCommand) {
        Validate.isTrue(tags.contains(cmd.tagId)) { "Tag with id ${cmd.tagId} does not exist on $displayName" }

        AggregateLifecycle.apply(
                TagUnlinkedEvent(
                        pictureId = pictureId,
                        tagId = cmd.tagId
                ),
                MetaData.with("AUDIT", "Tag (${cmd.tagId}) removed")
        )
    }

    @CommandHandler
    fun handle(cmd: DeletePictureCommand): PictureId {
        AggregateLifecycle.apply(
                PictureDeletedEvent(
                        pictureId = pictureId,
                        location = location,
                        physicallyDeleted = cmd.deletePhysicalFile
                ),
                MetaData.with("AUDIT",
                        if (cmd.deletePhysicalFile) "Picture deleted physically and from registry"
                        else "Picture deleted from registry"
                )
        )

        return pictureId
    }

    @CommandHandler
    fun handle(cmd: SetFileAttributesCommand) {
        AggregateLifecycle.apply(
                FileAttributesUpdatedEvent(
                        pictureId = pictureId,
                        fileSize = cmd.fileSize,
                        lastModifiedTime = cmd.lastModifiedTime,
                        imageWidth = cmd.imageWidth,
                        imageHeight = cmd.imageHeight,
                ),
                MetaData.with("AUDIT", "File attributes updated")
        )
    }

    @CommandHandler
    fun handle(cmd: SetContentHashCommand) {
        AggregateLifecycle.apply(
                ContentHashUpdatedEvent(
                        pictureId = pictureId,
                        contentHash = cmd.contentHash
                ),
                MetaData.with("AUDIT", "Content analysis completed")
        )
    }

    @CommandHandler
    fun handle(cmd: SetPictureAnalysisStatusCommand) {
        val auditMessage = when (cmd.status) {
            PictureAnalysisStatus.IN_PROGRESS -> "Picture analysis started"
            PictureAnalysisStatus.COMPLETED -> "Picture analysis completed"
        }

        AggregateLifecycle.apply(
                PictureAnalysisProgressEvent(
                        pictureId = pictureId,
                        status = cmd.status
                ),
                MetaData.with("AUDIT", auditMessage)
        )
    }

    @CommandHandler
    fun handle(cmd: LinkDuplicateCommand) {
        Assert.isFalse(duplicates.contains(cmd.targetId)) { "${cmd.targetId} is already linked as duplicate to $pictureId" }

        AggregateLifecycle.apply(
                DuplicateLinkedEvent(
                        pictureId = pictureId,
                        targetId = cmd.targetId,
                        similarity = cmd.similarity
                ),
                MetaData.with("AUDIT", "Duplicate (${cmd.targetId}) linked")
        )
    }

    @CommandHandler
    fun handle(cmd: UnlinkDuplicateCommand) {
        Assert.isTrue(duplicates.contains(cmd.targetId)) { "${cmd.targetId} is not linked to $pictureId" }

        AggregateLifecycle.apply(
                DuplicateUnlinkedEvent(
                        pictureId = pictureId,
                        targetId = cmd.targetId
                ),
                MetaData.with("AUDIT", "Duplicate (${cmd.targetId}) unlinked")
        )
    }

    @EventSourcingHandler
    fun on(evt: PictureCreatedEvent) {
        pictureId = evt.pictureId
        directoryId = evt.directoryId
        displayName = evt.displayName
        location = evt.location
    }

    @EventSourcingHandler
    fun on(evt: TagLinkedEvent) {
        tags = tags.plus(evt.tagId)
    }

    @EventSourcingHandler
    fun on(evt: TagUnlinkedEvent) {
        tags = tags.minus(evt.tagId)
    }

    @EventSourcingHandler
    fun on(evt: DuplicateLinkedEvent) {
        duplicates = duplicates.plus(evt.targetId)
    }

    @EventSourcingHandler
    fun on(evt: DuplicateUnlinkedEvent) {
        duplicates = duplicates.minus(evt.targetId)
    }

    @EventSourcingHandler
    fun on(evt: PictureMovedEvent) {
        directoryId = evt.targetDirectoryId
    }

    @EventSourcingHandler
    fun on(evt: PictureDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}

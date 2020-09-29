package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.Validate
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
import java.time.LocalDateTime
import java.util.*

@Aggregate
class PictureAggregate() {

    @AggregateIdentifier
    private lateinit var pictureId: PictureId
    private lateinit var displayName: String
    private lateinit var location: Path
    private var tags: Set<TagId> = emptySet()
    private var duplicates: Map<DuplicateMatchId, PictureId> = emptyMap()

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
        val targetLocation: Path =
                if (location.fileName == cmd.targetLocation.fileName) cmd.targetLocation
                else cmd.targetLocation.resolve(location.fileName)

        Validate.isFalse(location == targetLocation) { "Target location is the same as the current location" }

        AggregateLifecycle.apply(
                PictureMovedEvent(
                        pictureId = pictureId,
                        location = location,
                        targetLocation = targetLocation,
                ),
                MetaData.with("AUDIT", "Picture moved from $location to $targetLocation")
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
                        linkType = cmd.tagLinkType,
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

    fun setFileAttributes(
            fileSize: Long? = null,
            lastModifiedTime: LocalDateTime? = null,
            imageWidth: Int? = null,
            imageHeight: Int? = null,
    ) {
        AggregateLifecycle.apply(
                FileAttributesUpdatedEvent(
                        pictureId = pictureId,
                        fileSize = fileSize,
                        lastModifiedTime = lastModifiedTime,
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                ),
                MetaData.with("AUDIT", "File attributes updated")
        )
    }

    fun setContentHash(bitSet: BitSet) {
        AggregateLifecycle.apply(
                ContentHashUpdatedEvent(
                        pictureId = pictureId,
                        contentHash = bitSet
                ),
                MetaData.with("AUDIT", "Content analysis completed")
        )
    }

    fun linkDuplicate(targetId: PictureId, similarity: Float) {
        Assert.isFalse(duplicates.containsValue(targetId)) { "$targetId is already linked as duplicate to $pictureId" }

        AggregateLifecycle.apply(
                DuplicateLinkedEvent(
                        pictureId = pictureId,
                        targetId = targetId,
                        similarity = similarity,
                        matchId = DuplicateMatchId()
                ),
                MetaData.with("AUDIT", "Duplicate ($targetId) linked")
        )
    }

    fun unlinkDuplicate(matchId: DuplicateMatchId) {
        Assert.isTrue(duplicates.containsKey(matchId)) { "$matchId is not linked to $pictureId" }

        AggregateLifecycle.apply(
                DuplicateUnlinkedEvent(
                        pictureId = pictureId,
                        matchId = matchId
                ),
                MetaData.with("AUDIT", "Duplicate (${duplicates[matchId]}) unlinked")
        )
    }

    @EventSourcingHandler
    fun on(evt: PictureCreatedEvent) {
        pictureId = evt.pictureId
        displayName = evt.displayName
        this.location = evt.location
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
        duplicates = duplicates.plus(evt.matchId to evt.targetId)
    }

    @EventSourcingHandler
    fun on(evt: DuplicateUnlinkedEvent) {
        duplicates = duplicates.minus(evt.matchId)
    }

    @EventSourcingHandler
    fun on(evt: PictureMovedEvent) {
        location = evt.targetLocation
    }

    @EventSourcingHandler
    fun on(evt: PictureDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}

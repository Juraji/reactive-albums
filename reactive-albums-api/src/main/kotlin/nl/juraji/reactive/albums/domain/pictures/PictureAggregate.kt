package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.*
import nl.juraji.reactive.albums.domain.tags.TagId
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.common.Assert
import org.axonframework.eventsourcing.EventSourcingHandler
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

    fun getLocation(): Path = this.location

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
                )
        )
    }

    @CommandHandler
    fun handle(cmd: DeletePictureCommand) {
        AggregateLifecycle.apply(
                PictureDeletedEvent(
                        pictureId = cmd.pictureId
                )
        )
    }

    fun setFileAttributes(
            fileSize: Long? = null,
            lastModifiedTime: LocalDateTime? = null,
            imageWidth: Int? = null,
            imageHeight: Int? = null,
    ) {
        AggregateLifecycle.apply(
                AttributesUpdatedEvent(
                        pictureId = pictureId,
                        fileSize = fileSize,
                        lastModifiedTime = lastModifiedTime,
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                )
        )
    }

    fun setContentHash(bitSet: BitSet) {
        AggregateLifecycle.apply(
                ContentHashUpdatedEvent(
                        pictureId = pictureId,
                        contentHash = bitSet
                )
        )
    }

    fun addTag(tagId: TagId, tagLinkType: TagLinkType) {
        Validate.isFalse(tags.contains(tagId)) { "Tag with id $tagId is already present on $displayName" }

        AggregateLifecycle.apply(
                TagLinkedEvent(
                        pictureId = this.pictureId,
                        tagId = tagId,
                        linkType = tagLinkType,
                )
        )
    }

    fun removeTag(tagId: TagId) {
        Validate.isTrue(tags.contains(tagId)) { "Tag with id $tagId does not exist on $displayName" }

        AggregateLifecycle.apply(
                TagUnlinkedEvent(
                        pictureId = pictureId,
                        tagId = tagId
                )
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
                )
        )
    }

    fun unlinkDuplicate(matchId: DuplicateMatchId) {
        Assert.isTrue(duplicates.containsKey(matchId)) { "$matchId is not linked to $pictureId" }

        AggregateLifecycle.apply(
                DuplicateUnlinkedEvent(
                        pictureId = pictureId,
                        matchId = matchId
                )
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
    fun on(evt: PictureDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}

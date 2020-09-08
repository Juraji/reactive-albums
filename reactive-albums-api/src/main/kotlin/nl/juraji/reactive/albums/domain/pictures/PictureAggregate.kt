package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.*
import nl.juraji.reactive.albums.util.RgbColor
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
    private var tags: Set<TagEntity> = emptySet()
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

    fun addTag(label: String, labelColor: String, textColor: String, tagLinkType: TagLinkType) {
        Validate.isTrue(label.isNotBlank()) { "Tag label should not be blank" }
        Validate.isTrue(RgbColor.isHexColor(labelColor)) { "Tag label color should be a valid hexadecimal color" }
        Validate.isTrue(RgbColor.isHexColor(textColor)) { "Tag text color should be a valid hexadecimal color" }
        Validate.isTrue(tags.none { it.label == label }) { "Tag with label $label already exists on $displayName" }

        AggregateLifecycle.apply(
                TagAddedEvent(
                        pictureId = this.pictureId,
                        label = label,
                        labelColor = labelColor,
                        textColor = textColor,
                        linkType = tagLinkType,
                )
        )
    }

    fun removeTag(label: String) {
        Validate.isTrue(tags.any { it.label == label }) { "Tag with label $label does not exist on $displayName" }

        AggregateLifecycle.apply(
                TagRemovedEvent(
                        pictureId = pictureId,
                        label = label
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
    fun on(evt: TagAddedEvent) {
        tags = tags.plus(TagEntity(evt.label))
    }

    @EventSourcingHandler
    fun on(evt: TagRemovedEvent) {
        tags = tags.minus(TagEntity(evt.label))
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

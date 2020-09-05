package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.pictures.commands.*
import nl.juraji.reactive.albums.domain.pictures.events.*
import nl.juraji.reactive.albums.util.Colors
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class PictureAggregate() {

    @AggregateIdentifier
    private lateinit var pictureId: PictureId
    private lateinit var displayName: String

    @AggregateMember
    private var tags: Set<TagEntity> = emptySet()

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

        AggregateLifecycle.apply(AnalysisRequestedEvent(
                pictureId = cmd.pictureId,
                location = cmd.location,
        ))
    }

    @CommandHandler
    fun handle(cmd: UpdateAttributesCommand) {

        AggregateLifecycle.apply(
                AttributesUpdatedEvent(
                        pictureId = pictureId,
                        fileSize = cmd.fileSize,
                        lastModifiedTime = cmd.lastModifiedTime,
                        imageWidth = cmd.imageWidth,
                        imageHeight = cmd.imageHeight,
                )
        )
    }

    @CommandHandler
    fun handle(cmd: UpdateContentHashCommand) {
        AggregateLifecycle.apply(
                ContentHashUpdatedEvent(
                        pictureId = pictureId,
                        contentHash = cmd.contentHash
                )
        )
    }

    @CommandHandler
    fun handle(cmd: UpdateThumbnailLocationCommand) {
        AggregateLifecycle.apply(
                ThumbnailLocationUpdatedEvent(
                        pictureId = pictureId,
                        thumbnailLocation = cmd.thumbnailLocation,
                        thumbnailType = cmd.thumbnailType,
                )
        )
    }

    @CommandHandler
    fun handle(cmd: AddTagCommand) {
        Validate.isTrue(cmd.label.isNotBlank()) { "Tag label should not be blank" }
        Validate.isTrue(Colors.isHexColor(cmd.labelColor)) { "Tag label color should be a valid hexadecimal color" }
        Validate.isTrue(Colors.isHexColor(cmd.textColor)) { "Tag text color should be a valid hexadecimal color" }
        Validate.isTrue(tags.none { it.label == cmd.label }) { "Tag with label ${cmd.label} already exists on $displayName" }

        AggregateLifecycle.apply(
                TagAddedEvent(
                        pictureId = pictureId,
                        label = cmd.label,
                        labelColor = cmd.labelColor,
                        textColor = cmd.textColor,
                        linkType = cmd.tagLinkType,
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

    @CommandHandler
    fun handle(cmd: RemoveTagCommand) {
        Validate.isTrue(tags.any { it.label == cmd.label }) { "Tag with label ${cmd.label} does not exist on $displayName" }

        AggregateLifecycle.apply(
                TagRemovedEvent(
                        pictureId = pictureId,
                        label = cmd.label
                )
        )
    }

    @EventSourcingHandler
    fun on(evt: PictureCreatedEvent) {
        pictureId = evt.pictureId
        displayName = evt.displayName
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
    fun on(evt: PictureDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}

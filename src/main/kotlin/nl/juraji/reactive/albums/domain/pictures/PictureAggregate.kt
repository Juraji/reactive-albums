package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UpdatePictureAttributesCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureAnalysisRequestedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureAttributesUpdatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class PictureAggregate() {

    @AggregateIdentifier
    private lateinit var pictureId: PictureId

    @CommandHandler
    constructor(cmd: CreatePictureCommand) : this() {
        val displayName = cmd.displayName ?: cmd.location.fileName.toString()
        val pictureType = PictureType.byContentType(cmd.contentType)

        Validate.isNotNull(pictureType) { "Unsupported file type for $displayName" }

        AggregateLifecycle.apply(
                PictureCreatedEvent(
                        pictureId = cmd.pictureId,
                        displayName = displayName,
                        location = cmd.location,
                        pictureType = pictureType!!
                )
        )

        AggregateLifecycle.apply(PictureAnalysisRequestedEvent(
                pictureId = cmd.pictureId,
                location = cmd.location
        ))
    }

    @CommandHandler
    fun handle(cmd: UpdatePictureAttributesCommand) {

        AggregateLifecycle.apply(
                PictureAttributesUpdatedEvent(
                        pictureId = pictureId,
                        fileSize = cmd.fileSize,
                        lastModifiedTime = cmd.lastModifiedTime,
                        imageWidth = cmd.imageWidth,
                        imageHeight = cmd.imageHeight,
                        contentHash = cmd.contentHash,
                )
        )
    }

    @EventSourcingHandler
    fun on(evt: PictureCreatedEvent) {
        pictureId = evt.pictureId
    }
}

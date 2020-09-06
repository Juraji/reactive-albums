package nl.juraji.reactive.albums.processing

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.commands.UpdateThumbnailLocationCommand
import nl.juraji.reactive.albums.domain.pictures.events.AnalysisRequestedEvent
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class ThumbnailProcessor(
        private val commandGateway: CommandGateway,
        private val imageService: ImageService,
) {

    @EventSourcingHandler
    fun on(evt: AnalysisRequestedEvent) {
        logger.debug("Generating thumbnail for ${evt.pictureId}")

        imageService.createThumbnail(pictureId = evt.pictureId, source = evt.location)
                .map {
                    UpdateThumbnailLocationCommand(
                            pictureId = evt.pictureId,
                            thumbnailLocation = it,
                    )
                }
                .subscribe { commandGateway.send<Unit>(it) }
    }

    companion object : LoggerCompanion(ThumbnailProcessor::class)
}

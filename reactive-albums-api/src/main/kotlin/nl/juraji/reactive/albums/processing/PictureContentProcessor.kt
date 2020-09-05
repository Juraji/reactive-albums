package nl.juraji.reactive.albums.processing

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.commands.UpdateContentHashCommand
import nl.juraji.reactive.albums.domain.pictures.events.AnalysisRequestedEvent
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureContentProcessor(
        private val commandGateway: CommandGateway,
        private val imageService: ImageService,
) {

    @EventSourcingHandler
    fun on(evt: AnalysisRequestedEvent) {
        logger.info("Analyzing image content of ${evt.pictureId}")

        imageService.createContentHash(evt.location)
                .map {
                    UpdateContentHashCommand(
                            pictureId = evt.pictureId,
                            contentHash = it
                    )
                }
                .subscribe { commandGateway.send<Unit>(it) }
    }

    companion object : LoggerCompanion()
}

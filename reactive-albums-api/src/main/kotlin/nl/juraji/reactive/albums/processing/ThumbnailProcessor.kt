package nl.juraji.reactive.albums.processing

import nl.juraji.reactive.albums.configuration.PicturesAggregateConfiguration
import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.domain.pictures.commands.UpdateThumbnailLocationCommand
import nl.juraji.reactive.albums.domain.pictures.events.AnalysisRequestedEvent
import nl.juraji.reactive.albums.services.ImageService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class ThumbnailProcessor(
        private val commandGateway: CommandGateway,
        private val imageService: ImageService,
        private val pictureConfiguration: PicturesAggregateConfiguration,
) {

    @EventSourcingHandler
    fun on (evt: AnalysisRequestedEvent) {
        imageService.createThumbnail(pictureId = evt.pictureId, source = evt.location)
                .map {
                    UpdateThumbnailLocationCommand(
                            pictureId = evt.pictureId,
                            thumbnailLocation = it,
                            thumbnailType = PictureType.of(pictureConfiguration.thumbnailMimeType)!!,
                    )
                }
                .subscribe { commandGateway.send<Unit>(it) }
    }
}

package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.PicturesAggregateConfiguration
import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.domain.pictures.commands.UpdateThumbnailLocationCommand
import nl.juraji.reactive.albums.domain.pictures.events.AnalysisRequestedEvent
import nl.juraji.reactive.albums.domain.pictures.events.ThumbnailLocationUpdatedEvent
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class GeneratePictureThumbnailSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var imageService: ImageService

    @Autowired
    private lateinit var pictureConfiguration: PicturesAggregateConfiguration

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: AnalysisRequestedEvent) {
        imageService.runCatching {
            createThumbnail(
                    source = evt.location,
                    targetDirectory = pictureConfiguration.thumbnailPath,
                    name = evt.pictureId.identifier,
                    size = pictureConfiguration.thumbnailSize,
                    pictureType = pictureConfiguration.thumbnailMimeType
            )
        }
                .onSuccess { location ->
                    commandGateway.send<Unit>(UpdateThumbnailLocationCommand(
                            pictureId = evt.pictureId,
                            thumbnailLocation = location,
                            thumbnailType = PictureType.of(pictureConfiguration.thumbnailMimeType)!!,
                    ))
                }
                .onFailure { ex -> logger.error("Thumbnail creation for ${evt.pictureId} failed", ex) }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: ThumbnailLocationUpdatedEvent) {
        logger.debug("Successfully created thumbnail for ${evt.pictureId} at ${evt.thumbnailLocation}")
    }

    companion object : LoggerCompanion()
}

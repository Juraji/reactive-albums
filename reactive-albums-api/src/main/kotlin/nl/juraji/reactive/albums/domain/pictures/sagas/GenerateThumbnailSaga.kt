package nl.juraji.reactive.albums.domain.pictures.sagas

import nl.juraji.reactive.albums.configuration.PicturesAggregateConfiguration
import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.events.PictureAnalysisRequestedEvent
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Paths

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class GenerateThumbnailSaga {

    @Autowired
    private lateinit var imageService: ImageService

    @Autowired
    private lateinit var pictureConfiguration: PicturesAggregateConfiguration

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureAnalysisRequestedEvent) {
        imageService.runCatching {
            val target = Paths.get(
                    pictureConfiguration.thumbnailLocation,
                    "${evt.pictureId.identifier}.${pictureConfiguration.thumbnailMimeType.type}"
            )

            createThumbnail(
                    source = evt.location,
                    target = target,
                    size = pictureConfiguration.thumbnailSize,
                    pictureType = pictureConfiguration.thumbnailMimeType
            )
        }
                .onSuccess { location ->
                    logger.debug("Successfully created thumbnail for ${evt.pictureId} at $location")
                    SagaLifecycle.end()
                }
                .onFailure { ex -> logger.error("Thumbnail creation for ${evt.pictureId} failed", ex) }
    }

    companion object : LoggerCompanion()
}

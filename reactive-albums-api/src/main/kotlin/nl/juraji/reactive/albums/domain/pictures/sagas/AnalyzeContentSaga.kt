package nl.juraji.reactive.albums.domain.pictures.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.UpdatePictureAttributesCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureAnalysisRequestedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureAttributesUpdatedEvent
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class AnalyzeContentSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var imageService: ImageService

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureAnalysisRequestedEvent) {
        imageService.runCatching { createContentHash(evt.location) }
                .onSuccess {
                    commandGateway.send<Unit>(
                            UpdatePictureAttributesCommand(
                                    pictureId = evt.pictureId,
                                    contentHash = it
                            )
                    )
                }
                .onFailure { onException(it, evt.pictureId) }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun onEvent(evt: PictureAttributesUpdatedEvent) {
        logger.debug("Image content analyzed for ${evt.pictureId}")
    }

    private fun onException(throwable: Throwable, pictureId: PictureId) {
        logger.error("Content analysis failed for $pictureId", throwable)
        SagaLifecycle.end()
    }

    companion object : LoggerCompanion() {}
}

package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.services.analysis.PictureAttributesAnalysisService
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class TEMPAnalyzePictureSaga {
    /**
     * TODO: Temporary saga for analyzing new pictures.
     * Should be replaced by filesystem watch service
     */

    @Autowired
    private lateinit var pictureAttributesAnalysisService: PictureAttributesAnalysisService

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureCreatedEvent) {
        pictureAttributesAnalysisService.analyzePicture(evt.pictureId, evt.location).block()
        SagaLifecycle.end()
    }
}

package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.services.analysis.DirectoryAnalysisService
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class TEMPAnalyzeRegisteredDirectorySaga {
    /**
     * TODO: Temporary saga for analyzing new directories.
     * Should be replaced by filesystem watch service
     */

    @Autowired
    private lateinit var directoryAnalysisService: DirectoryAnalysisService

    @StartSaga
    @SagaEventHandler(associationProperty = "directoryId")
    fun on(evt: DirectoryRegisteredEvent) {
        directoryAnalysisService.analyzeDirectory(evt.directoryId, evt.location).block()
        SagaLifecycle.end()
    }
}

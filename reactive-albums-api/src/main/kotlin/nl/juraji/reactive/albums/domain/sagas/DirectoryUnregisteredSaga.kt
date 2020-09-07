package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class DirectoryUnregisteredSaga {

    @Autowired
    private lateinit var pictureRepository: ReactivePictureRepository

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "directoryId")
    fun on(evt: DirectoryUnregisteredEvent) {
        pictureRepository.findAllByDirectoryId(evt.directoryId.identifier)
                .map { DeletePictureCommand(pictureId = PictureId(it.id)) }
                .map { commandGateway.sendAndWait<PictureId>(it) }
                .doOnNext { SagaLifecycle.associateWith("pictureId", it.toString()) }
                .blockLast()
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureDeletedEvent) {
        TODO("Check if saga ends when all events are completed")
    }
}

package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.query.projections.repositories.SyncPictureRepository
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class DirectoryUnregisteredSaga : ForkJoinSaga<PictureId>("pictureId") {

    @Autowired
    private lateinit var syncPictureRepository: SyncPictureRepository

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "directoryId")
    fun on(evt: DirectoryUnregisteredEvent) {
        logger.debug("Deleting pictures associated to ${evt.directoryId}")

        syncPictureRepository.findAllByDirectoryId(evt.directoryId.identifier)
                .map { DeletePictureCommand(pictureId = PictureId(it.id)) }
                .forEach {
                    commandGateway.send<Unit>(it)
                            .thenRun { forkedEventKey(it.pictureId) }
                }
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureDeletedEvent) {
        onForkedEventHandled(evt.pictureId)
    }

    companion object : LoggerCompanion(DirectoryUnregisteredSaga::class)
}

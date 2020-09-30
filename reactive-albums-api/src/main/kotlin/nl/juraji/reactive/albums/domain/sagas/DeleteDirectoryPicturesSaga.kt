package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.query.projections.repositories.SyncPictureRepository
import nl.juraji.reactive.albums.util.LoggerCompanion
import nl.juraji.reactive.albums.util.SagaAssociations
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.annotation.DeadlineHandler
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class DeleteDirectoryPicturesSaga {

    @Autowired
    private lateinit var syncPictureRepository: SyncPictureRepository

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var deadlineManager: DeadlineManager

    @StartSaga
    @SagaEventHandler(associationProperty = "directoryId")
    fun on(evt: DirectoryUnregisteredEvent) {
        logger.debug("Deleting pictures associated to ${evt.directoryId}")
        deadlineManager.schedule(Duration.ofMinutes(10), PictureColorTagSaga.SAGA_DEADLINE)

        syncPictureRepository.findAllByDirectoryId(evt.directoryId.identifier)
                .map { DeletePictureCommand(pictureId = PictureId(it.id)) }
                .forEach { cmd ->
                    SagaAssociations.associateWith("pictureId", cmd.pictureId.identifier)
                    commandGateway.send<Any>(cmd).exceptionally {
                        SagaAssociations.removeAssociationWith("pictureId", cmd.pictureId.identifier)
                    }
                }
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureDeletedEvent) {
        SagaAssociations.removeAssociationWith("pictureId", evt.pictureId.identifier)

        if (SagaAssociations.hasAssociationKey("pictureId")) {
            deadlineManager.cancelAllWithinScope(SAGA_DEADLINE)
            SagaLifecycle.end()
        }
    }

    @DeadlineHandler(deadlineName = SAGA_DEADLINE)
    fun onDeadline() {
        SagaLifecycle.end()
    }

    companion object : LoggerCompanion(DeleteDirectoryPicturesSaga::class) {
        const val SAGA_DEADLINE = "DeleteDirectoryPicturesSagaDeadline"
    }
}

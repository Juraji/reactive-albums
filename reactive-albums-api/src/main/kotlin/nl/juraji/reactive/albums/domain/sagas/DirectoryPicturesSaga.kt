package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureMovedEvent
import nl.juraji.reactive.albums.services.CommandDispatch
import nl.juraji.reactive.albums.util.SagaAssociations
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class DirectoryPicturesSaga {

    @Autowired
    private lateinit var commandDispatch: CommandDispatch

    @StartSaga
    @SagaEventHandler(associationProperty = DIRECTORY_ASSOC_KEY)
    fun on(evt: DirectoryRegisteredEvent) {
    }

    @SagaEventHandler(associationProperty = DIRECTORY_ASSOC_KEY)
    fun on(evt: PictureCreatedEvent) {
        SagaAssociations.associateWith(PICTURE_ASSOC_KEY, evt.pictureId.identifier)
    }

    @SagaEventHandler(associationProperty = "sourceDirectoryId", keyName = DIRECTORY_ASSOC_KEY)
    fun onMoveFrom(evt: PictureMovedEvent) {
        SagaAssociations.removeAssociationWith(PICTURE_ASSOC_KEY, evt.pictureId.identifier)
    }

    @SagaEventHandler(associationProperty = "targetDirectoryId", keyName = DIRECTORY_ASSOC_KEY)
    fun onMoveTo(evt: PictureMovedEvent) {
        SagaAssociations.associateWith(PICTURE_ASSOC_KEY, evt.pictureId.identifier)
    }

    @SagaEventHandler(associationProperty = PICTURE_ASSOC_KEY)
    fun on(evt: PictureDeletedEvent) {
        SagaAssociations.removeAssociationWith(PICTURE_ASSOC_KEY, evt.pictureId.identifier)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = DIRECTORY_ASSOC_KEY)
    fun on(evt: DirectoryUnregisteredEvent) {
        val associatedPictureIds: List<PictureId> = SagaAssociations.getAssociatedValues(PICTURE_ASSOC_KEY) { PictureId(it) }

        associatedPictureIds
                .map { DeletePictureCommand(pictureId = it) }
                .forEach { commandDispatch.dispatchAndForget(it) }
    }

    companion object {
        const val DIRECTORY_ASSOC_KEY = "directoryId"
        const val PICTURE_ASSOC_KEY = "pictureId"
    }
}

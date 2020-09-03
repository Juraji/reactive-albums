package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryScanRequestedEvent
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.*
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Path
import java.nio.file.Paths

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class ScanDirectorySaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var pictureRepository: PictureRepository

    @Autowired
    private lateinit var fileSystemService: FileSystemService

    @StartSaga
    @SagaEventHandler(associationProperty = "directoryId")
    fun on(evt: DirectoryScanRequestedEvent) {
        val directoryFiles = fileSystemService.listFiles(evt.location);

        if (!evt.firstTime) {
            pictureRepository.findAllByLocationStartsWith(evt.location.toString())
                    .map { it.id to Paths.get(it.location) }
                    .filter { (_, location) -> directoryFiles.none { f -> f === location } }
                    .forEach { (pictureId) ->
                        commandGateway.send<Unit>(DeletePictureCommand(
                                pictureId = pictureId
                        )).thenRun { SagaLifecycle.associateWith(DELETE_PICTURE_ASSOCIATION, pictureId.toString()) }
                    }
        }

        directoryFiles
                .filter { !pictureRepository.existsByLocation(it.toString()) }
                .forEach { location ->
                    val contentType = fileSystemService.readContentType(location);

                    commandGateway.send<PictureId>(
                            CreatePictureCommand(
                                    pictureId = PictureId(),
                                    location = location,
                                    contentType = contentType,
                            )
                    ).thenAccept() { pictureId -> SagaLifecycle.associateWith(ADD_PICTURE_ASSOCIATION, pictureId.toString()) }
                }
    }

    @SagaEventHandler(associationProperty = "pictureId", keyName = ADD_PICTURE_ASSOCIATION)
    fun on(evt: PictureCreatedEvent) {
        SagaLifecycle.removeAssociationWith(ADD_PICTURE_ASSOCIATION, evt.pictureId.toString())
        checkAllAssociatedEventsHandled();
    }

    @SagaEventHandler(associationProperty = "pictureId", keyName = DELETE_PICTURE_ASSOCIATION)
    fun on(evt: PictureDeletedEvent) {
        SagaLifecycle.removeAssociationWith(ADD_PICTURE_ASSOCIATION, evt.pictureId.toString())
        checkAllAssociatedEventsHandled();
    }

    private fun checkAllAssociatedEventsHandled(){
        val scope = SagaLifecycle.getCurrentScope<AnnotatedSaga<Any>>()

        if (scope.associationValues.none { it.key == ADD_PICTURE_ASSOCIATION || it.key == DELETE_PICTURE_ASSOCIATION }) {
            logger.debug("Directory scan completed")
            SagaLifecycle.end()
        }
    }

    companion object : LoggerCompanion() {
        private const val ADD_PICTURE_ASSOCIATION = "add-pictureId"
        private const val DELETE_PICTURE_ASSOCIATION = "delete-pictureId"
    }
}

package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.util.LoggerCompanion
import nl.juraji.reactive.albums.util.extensions.toList
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Path

@Saga
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class TEMPAnalyzeRegisteredDirectorySaga : ForkJoinSaga<PictureId>("pictureId") {
    /**
     * TODO: Temporary saga for analyzing new directories.
     * Should be replaced by filesystem watch service
     */

    @Autowired
    private lateinit var fileSystemService: FileSystemService

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "directoryId")
    fun on(evt: DirectoryRegisteredEvent) {
        logger.debug("Analyzing directory ${evt.directoryId} (${evt.location})")

        val directoryFiles: List<Path> = fileSystemService.listFiles(evt.location).toList()

        directoryFiles
                .map { createPictureCommand(evt.directoryId, it) }
                .map { commandGateway.send<PictureId>(it) }
                .forEach { it.thenAccept { id -> forkedEventKey(id) } }
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureCreatedEvent) {
        onForkedEventHandled(evt.pictureId)
    }

    private fun createPictureCommand(directoryId: DirectoryId, location: Path): CreatePictureCommand {
        return CreatePictureCommand(
                pictureId = PictureId(),
                location = location,
                contentType = fileSystemService.readContentType(location).block() ?: "",
                directoryId = directoryId
        )
    }

    companion object : LoggerCompanion(TEMPAnalyzeRegisteredDirectorySaga::class)
}

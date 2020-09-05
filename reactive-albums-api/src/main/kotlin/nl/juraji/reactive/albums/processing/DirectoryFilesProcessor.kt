package nl.juraji.reactive.albums.processing

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryScanRequestedEvent
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.query.projections.PictureLocationProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.extra.bool.not
import java.nio.file.Path

@Service
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class DirectoryFilesProcessor(
        private val commandGateway: CommandGateway,
        private val pictureRepository: ReactivePictureRepository,
        private val fileSystemService: FileSystemService,
) {

    @EventSourcingHandler
    fun on(evt: DirectoryScanRequestedEvent) {
        logger.info("Updating against directory ${evt.location}")

        val directoryFiles: Flux<Path> = fileSystemService
                .listFiles(evt.location)
                .cache()
        val knownPictures: Flux<PictureLocationProjection> = pictureRepository
                .findAllByParentLocation(evt.location.toString())
                .cache()

        deleteMissingFiles(knownPictures, directoryFiles)
        addNewFiles(knownPictures, directoryFiles)
    }

    private fun deleteMissingFiles(knownPictures: Flux<PictureLocationProjection>, directoryFiles: Flux<Path>) {
        knownPictures
                .filterWhen { picture ->
                    directoryFiles.any { location -> picture.parentLocation == location.toString() }.not()
                }
                .map { picture ->
                    DeletePictureCommand(
                            pictureId = PictureId(picture.id)
                    )
                }
                .subscribe { commandGateway.send<Unit>(it) }
    }

    private fun addNewFiles(knownPictures: Flux<PictureLocationProjection>, directoryFiles: Flux<Path>) {
        directoryFiles
                .filterWhen { location ->
                    knownPictures.any { pic -> pic.location == location.toString() }.not()
                }
                .flatMap { path -> Mono.zip(Mono.just(path), fileSystemService.readContentType(path)) }
                .map { (location, contentType) ->
                    CreatePictureCommand(
                            pictureId = PictureId(),
                            location = location,
                            contentType = contentType,
                    )
                }
                .subscribe { commandGateway.send<Unit>(it) }
    }

    companion object : LoggerCompanion()
}

package nl.juraji.reactive.albums.services.analysis

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.extra.bool.not
import java.nio.file.Path

@Service
class DirectoryAnalysisService(
        private val pictureRepository: ReactivePictureRepository,
        private val fileSystemService: FileSystemService,
        private val commandGateway: CommandGateway,
) {

    fun analyzeDirectory(directoryId: DirectoryId, location: Path): Mono<Unit> {
        logger.debug("Analyzing directory $directoryId ($location)")

        val directoryFiles: Flux<Path> = fileSystemService
                .listFiles(location)
                .cache()
        val knownPictures: Flux<PictureProjection> = pictureRepository
                .findAllByDirectoryId(directoryId.identifier)
                .cache()

        return Flux.concat(
                deleteMissingFiles(knownPictures, directoryFiles),
                addNewFiles(directoryId, knownPictures, directoryFiles)
        ).last()
    }

    private fun deleteMissingFiles(knownPictures: Flux<PictureProjection>, directoryFiles: Flux<Path>): Flux<Unit> {
        return knownPictures
                .filterWhen { picture ->
                    directoryFiles.any { location -> picture.parentLocation == location.toString() }.not()
                }
                .map { picture ->
                    DeletePictureCommand(
                            pictureId = PictureId(picture.id)
                    )
                }
                .map { commandGateway.sendAndWait<Unit>(it) }
    }

    private fun addNewFiles(directoryId: DirectoryId, knownPictures: Flux<PictureProjection>, directoryFiles: Flux<Path>): Flux<Unit> {
        return directoryFiles
                .filterWhen { location ->
                    knownPictures.any { pic -> pic.location == location.toString() }.not()
                }
                .flatMap { path -> Mono.zip(Mono.just(path), fileSystemService.readContentType(path)) }
                .map { (location, contentType) ->
                    CreatePictureCommand(
                            pictureId = PictureId(),
                            location = location,
                            contentType = contentType,
                            directoryId = directoryId
                    )
                }
                .map { commandGateway.sendAndWait<Unit>(it) }
    }

    companion object : LoggerCompanion(DirectoryAnalysisService::class)
}

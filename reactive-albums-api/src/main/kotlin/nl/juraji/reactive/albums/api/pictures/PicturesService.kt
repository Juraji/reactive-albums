package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.domain.ValidateAsync
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import nl.juraji.reactive.albums.services.FileSystemService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.nio.file.Path

@Service
class PicturesService(
        private val pictureRepository: ReactivePictureRepository,
        private val fileSystemService: FileSystemService,
        private val commandGateway: CommandGateway,
) {

    fun addPicture(
            location: Path,
            displayName: String?,
    ): Mono<PictureProjection> {
        return ValidateAsync.all(
                ValidateAsync.isFalse(pictureRepository.existsByLocation(location.toString())) { "A picture with location $location already exists" },
                ValidateAsync.isTrue(fileSystemService.exists(location).toMono()) { "File at $location does not exist" }
        ).flatMap {
            val contentType = fileSystemService.readContentType(location)

            val command = CreatePictureCommand(
                    pictureId = PictureId(),
                    location = location,
                    contentType = contentType,
                    displayName = displayName
            )

            commandGateway.send<PictureId>(command).toMono()
                    .flatMap { pictureRepository.subscribeFirst { it.id == command.pictureId.identifier } }
        }
    }
}

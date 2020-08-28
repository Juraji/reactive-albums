package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.handlers.FindPictureByIdQuery
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.util.extensions.subscribeToNextUpdate
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.file.Path

@Service
class PicturesService(
        private val pictureRepository: PictureRepository,
        private val fileSystemService: FileSystemService,
        private val commandGateway: CommandGateway,
        private val queryGateway: QueryGateway,
) {

    fun addPicture(
            location: Path,
            displayName: String?,
    ): Mono<PictureProjection> {
        Validate.isFalse(pictureRepository.existsByLocation(location.toString())) { "A picture with location $location already exists" }
        Validate.isTrue(fileSystemService.exists(location)) { "File at $location does not exist" }
        val contentType = fileSystemService.readContentType(location)

        val command = CreatePictureCommand(
                pictureId = PictureId(),
                location = location,
                contentType = contentType,
                displayName = displayName
        )

        commandGateway.sendAndWait<PictureId>(command)
        return queryGateway.subscribeToNextUpdate(
                FindPictureByIdQuery(command.pictureId),
                PictureProjection::class
        )
    }
}

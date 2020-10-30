package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.*
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.services.CommandDispatch
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.nio.file.Paths
import java.time.Duration

@Service
class PictureCommandsService(
        private val commandDispatch: CommandDispatch,
        private val directoryRepository: DirectoryRepository,
        private val pictureRepository: PictureRepository,
) {
    fun rescanDuplicates(pictureId: String): Mono<PictureId> =
            commandDispatch.dispatch(ScanDuplicatesCommand(pictureId = PictureId(pictureId)))

    fun unlinkDuplicateMatch(pictureId: String, targetId:String): Mono<Void> =
            commandDispatch.dispatch(UnlinkDuplicateCommand(
                    pictureId = PictureId(pictureId),
                    targetId = PictureId(targetId)
            ))

    fun movePicture(pictureId: String, targetDirectoryId: String): Mono<PictureProjection> =
            directoryRepository.findById(targetDirectoryId)
                    .map {
                        MovePictureCommand(
                                pictureId = PictureId(pictureId),
                                targetDirectoryId = DirectoryId(targetDirectoryId),
                                targetLocation = Paths.get(it.location),
                        )
                    }
                    .flatMap { commandDispatch.dispatch<PictureId>(it) }
                    .flatMap { id -> pictureRepository.subscribeFirst(updateTimeout) { it.id == id.identifier } }

    fun deletePicture(pictureId: String, deletePhysicalFile: Boolean): Mono<PictureId> =
            commandDispatch.dispatch(DeletePictureCommand(pictureId = PictureId(pictureId), deletePhysicalFile = deletePhysicalFile))

    fun linkTag(pictureId: String, tagId: String): Flux<TagProjection> =
            commandDispatch.dispatch<PictureId>(LinkTagCommand(pictureId = PictureId(pictureId), tagId = TagId(tagId)))
                    .flatMap { id -> pictureRepository.subscribeFirst(updateTimeout) { it.id == id.identifier } }
                    .flatMapMany { it.tags.toFlux() }

    fun unlinkTag(pictureId: String, tagId: String): Flux<TagProjection> =
            commandDispatch.dispatch<PictureId>(UnlinkTagCommand(pictureId = PictureId(pictureId), tagId = TagId(tagId)))
                    .flatMap { id -> pictureRepository.subscribeFirst(updateTimeout) { it.id == id.identifier } }
                    .flatMapMany { it.tags.toFlux() }

    companion object {
        val updateTimeout: Duration = Duration.ofSeconds(30)
    }
}

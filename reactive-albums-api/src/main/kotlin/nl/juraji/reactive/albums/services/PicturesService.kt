package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.api.CommandSenderService
import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.pictures.commands.*
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.query.projections.repositories.TagRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.file.Paths
import java.time.Duration

@Service
class PicturesService(
        commandGateway: CommandGateway,
        private val duplicateMatchRepository: DuplicateMatchRepository,
        private val pictureRepository: PictureRepository,
        private val tagRepository: TagRepository,
) : CommandSenderService(commandGateway) {

    fun rescanDuplicates(pictureId: String): Mono<PictureId> = send(ScanDuplicatesCommand(pictureId = PictureId(pictureId)))

    fun unlinkDuplicateMatch(pictureId: String, matchId: String): Mono<Void> {
        val sourceDuplicate = send<Void>(UnlinkDuplicateCommand(
                pictureId = PictureId(pictureId),
                matchId = DuplicateMatchId(matchId)
        ))

        val targetDuplicate = duplicateMatchRepository
                .findInverseMatchByMatchId(matchId = matchId)
                .map { UnlinkDuplicateCommand(pictureId = PictureId(it.pictureId), matchId = DuplicateMatchId(it.id)) }
                .flatMap { sendAndCatch<Void>(it) }

        return sourceDuplicate.and(targetDuplicate)
    }

    fun movePicture(pictureId: String, targetLocation: String): Mono<PictureProjection> = send<PictureId>(
            MovePictureCommand(
                    pictureId = PictureId(pictureId),
                    targetLocation = Paths.get(targetLocation)
            )
    ).flatMap { id -> pictureRepository.subscribeFirst(updateTimeout) { it.id == id.identifier } }

    fun deletePicture(pictureId: String, deletePhysicalFile: Boolean): Mono<String> = send<PictureId>(
            DeletePictureCommand(
                    pictureId = PictureId(pictureId),
                    deletePhysicalFile = deletePhysicalFile
            )
    ).map { it.identifier }

    fun linkTag(pictureId: String, tagId: String): Mono<TagProjection> = send<Any>(
            LinkTagCommand(
                    pictureId = PictureId(pictureId),
                    tagId = TagId(tagId),
                    tagLinkType = TagLinkType.USER
            )
    ).flatMap { tagRepository.findById(tagId) }

    fun unlinkTag(pictureId: String, tagId: String): Mono<Void> = send(
            UnlinkTagCommand(
                    pictureId = PictureId(pictureId),
                    tagId = TagId(tagId),
            )
    )

    companion object {
        val updateTimeout: Duration = Duration.ofSeconds(30)
    }
}

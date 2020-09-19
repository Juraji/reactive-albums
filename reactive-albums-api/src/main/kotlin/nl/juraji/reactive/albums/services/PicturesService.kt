package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.api.CommandSenderService
import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.pictures.commands.*
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PicturesService(
        commandGateway: CommandGateway,
        private val duplicateMatchRepository: DuplicateMatchRepository,
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

    fun deletePicture(pictureId: String, deletePhysicalFile: Boolean): Mono<String> = send<PictureId>(
            DeletePictureCommand(
                    pictureId = PictureId(pictureId),
                    deletePhysicalFile = deletePhysicalFile
            )
    ).map { it.identifier }

    fun linkTag(pictureId: String, tagId: String): Mono<Void> = send(
            LinkTagCommand(
                    pictureId = PictureId(pictureId),
                    tagId = TagId(tagId),
                    tagLinkType = TagLinkType.USER
            )
    )

    fun unlinkTag(pictureId: String, tagId: String): Mono<Void> = send(
            UnlinkTagCommand(
                    pictureId = PictureId(pictureId),
                    tagId = TagId(tagId),
            )
    )

}

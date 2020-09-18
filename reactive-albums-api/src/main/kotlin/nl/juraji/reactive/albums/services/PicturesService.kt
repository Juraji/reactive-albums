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

    fun rescanDuplicates(pictureId: PictureId): Mono<Void> =
            send(ScanDuplicatesCommand(pictureId = pictureId))

    fun unlinkDuplicateMatch(pictureId: PictureId, matchId: DuplicateMatchId): Mono<Void> {
        val sourceDuplicate = send<Void>(UnlinkDuplicateCommand(pictureId = pictureId, matchId = matchId))

        val targetDuplicate = duplicateMatchRepository
                .findInverseMatchByMatchId(matchId = matchId.identifier)
                .map { UnlinkDuplicateCommand(pictureId = PictureId(it.pictureId), matchId = DuplicateMatchId(it.id)) }
                .flatMap { sendAndCatch<Void>(it) }

        return sourceDuplicate.and(targetDuplicate)
    }

    fun deletePicture(pictureId: PictureId): Mono<Void> =
            send(DeletePictureCommand(pictureId = pictureId))

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

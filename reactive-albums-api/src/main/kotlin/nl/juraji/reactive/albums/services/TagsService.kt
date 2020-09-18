package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.api.CommandSenderService
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.domain.tags.commands.CreateTagCommand
import nl.juraji.reactive.albums.domain.tags.commands.DeleteTagCommand
import nl.juraji.reactive.albums.domain.tags.commands.UpdateTagCommand
import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.query.projections.repositories.TagRepository
import nl.juraji.reactive.albums.util.RgbColor
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class TagsService(
        commandGateway: CommandGateway,
        private val tagRepository: TagRepository,
) : CommandSenderService(commandGateway) {

    fun createTag(label: String, tagColor: String?, textColor: String?): Mono<TagProjection> {
        val cmd = CreateTagCommand(
                tagId = TagId(),
                label = label,
                tagColor = tagColor?.let { RgbColor.of(it) },
                textColor = textColor?.let { RgbColor.of(it) },
        )

        return Mono.just(cmd)
                .flatMap { send<TagId>(it).toMono() }
                .flatMap { id -> tagRepository.subscribeFirst { it.id == id.identifier } }
    }

    fun updateTag(tagId: String, label: String?, tagColor: String?, textColor: String?): Mono<TagProjection> {
        val cmd = UpdateTagCommand(
                tagId = TagId(tagId),
                label = label,
                tagColor = tagColor?.let { RgbColor.of(it) },
                textColor = textColor?.let { RgbColor.of(it) },
        )

        return Mono.just(cmd)
                .flatMap { send<TagId>(it).toMono() }
                .flatMap { id -> tagRepository.subscribeFirst { it.id == id.identifier } }
    }

    fun deleteTag(tagId: String): Mono<TagId> = send(DeleteTagCommand(tagId = TagId(tagId)))
}

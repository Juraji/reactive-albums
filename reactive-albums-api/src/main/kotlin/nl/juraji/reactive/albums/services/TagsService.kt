package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.api.CommandSenderService
import nl.juraji.reactive.albums.domain.ValidateAsync
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
import java.time.Duration

@Service
class TagsService(
        commandGateway: CommandGateway,
        private val tagRepository: TagRepository,
) : CommandSenderService(commandGateway) {

    fun createTag(label: String, tagColor: String?, textColor: String?): Mono<TagProjection> = ValidateAsync
            .isFalse(tagRepository.existsByLabel(label)) { "Duplicate tags are not allowed" }
            .map {
                CreateTagCommand(
                        tagId = TagId(),
                        label = label,
                        tagColor = tagColor?.let { RgbColor.of(it) },
                        textColor = textColor?.let { RgbColor.of(it) },
                )
            }
            .flatMap { send<TagId>(it) }
            .flatMap { id -> tagRepository.subscribeFirst(updateTimeout) { it.id == id.identifier } }

    fun updateTag(tagId: String, label: String?, tagColor: String?, textColor: String?): Mono<TagProjection> = ValidateAsync
            .ignoreWhen(label == null) { isFalse(tagRepository.existsByLabel(label!!)) { "Duplicate tags are not allowed" } }
            .map {
                UpdateTagCommand(
                        tagId = TagId(tagId),
                        label = label,
                        tagColor = tagColor?.let { RgbColor.of(it) },
                        textColor = textColor?.let { RgbColor.of(it) },
                )
            }
            .flatMap { send<TagId>(it) }
            .flatMap { id -> tagRepository.subscribeFirst(updateTimeout) { it.id == id.identifier } }

    fun deleteTag(tagId: String): Mono<TagId> = send(DeleteTagCommand(tagId = TagId(tagId)))

    companion object {
        val updateTimeout: Duration = Duration.ofSeconds(30)
    }
}

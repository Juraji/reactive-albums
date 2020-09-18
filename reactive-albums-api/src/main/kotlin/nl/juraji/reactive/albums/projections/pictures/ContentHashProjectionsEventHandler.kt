package nl.juraji.reactive.albums.projections.pictures

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.events.ContentHashUpdatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class ContentHashProjectionsEventHandler(
        private val contentHashRepository: ContentHashRepository,
) {

    @EventHandler
    fun on(evt: ContentHashUpdatedEvent) {
        contentHashRepository
                .findById(evt.pictureId.identifier)
                .map { it.copy(contentHash = evt.contentHash) }
                .switchIfEmpty {
                    Mono.just(ContentHashProjection(
                            pictureId = evt.pictureId.identifier,
                            contentHash = evt.contentHash
                    ))
                }
                .flatMap { contentHashRepository.save(it) }
                .block()
    }

    @EventHandler
    fun on(evt: PictureDeletedEvent) {
        contentHashRepository
                .deleteById(evt.pictureId.identifier)
                .block()
    }
}

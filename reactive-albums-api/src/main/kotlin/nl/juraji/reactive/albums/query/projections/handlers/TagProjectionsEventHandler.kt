package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.tags.events.TagCreatedEvent
import nl.juraji.reactive.albums.domain.tags.events.TagDeletedEvent
import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.query.projections.repositories.TagRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class TagProjectionsEventHandler(
        private val tagRepository: TagRepository,
) {

    @EventHandler
    fun on(evt: TagCreatedEvent) {
        val entity = TagProjection(
                id = evt.tagId.identifier,
                label = evt.label,
                tagColor = evt.tagColor.toHexString(),
                textColor = evt.textColor.toHexString(),
        )

        tagRepository.save(entity).block()
    }

    @EventHandler
    fun on(evt: TagDeletedEvent) {
        tagRepository.deleteById(evt.tagId.identifier).block()
    }
}

package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.domain.tags.events.TagCreatedEvent
import nl.juraji.reactive.albums.query.projections.ColorTagProjection
import nl.juraji.reactive.albums.query.projections.repositories.ColorTagRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class ColorTagProjectionsEventHandler(
        private val colorTagRepository: ColorTagRepository,
) {

    @EventHandler
    fun on(evt: TagCreatedEvent) {
        if (evt.tagType == TagType.COLOR) {
            val entity = ColorTagProjection(
                    id = evt.tagId.identifier,
                    red = evt.tagColor.red,
                    green = evt.tagColor.green,
                    blue = evt.tagColor.blue,
            )

            colorTagRepository.save(entity)
        }
    }

    @ResetHandler
    fun onReset() {
        colorTagRepository.deleteAll()
    }
}

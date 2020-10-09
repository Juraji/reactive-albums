package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.domain.tags.events.TagCreatedEvent
import nl.juraji.reactive.albums.domain.tags.events.TagDeletedEvent
import nl.juraji.reactive.albums.query.projections.ColorTagLUTProjection
import nl.juraji.reactive.albums.query.projections.repositories.ColorTagLUTRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class ColorTagLUTProjectionsEventHandler(
        private val colorTagLUTRepository: ColorTagLUTRepository,
) {

    @EventHandler
    fun on(evt: TagCreatedEvent) {
        if (evt.tagType == TagType.COLOR) {
            val entity = ColorTagLUTProjection(
                    tagId = evt.tagId.identifier,
                    red = evt.tagColor.red,
                    green = evt.tagColor.green,
                    blue = evt.tagColor.blue,
            )

            colorTagLUTRepository.save(entity)
        }
    }

    @EventHandler
    fun on(evt: TagDeletedEvent) {
        colorTagLUTRepository.runCatching { deleteById(evt.tagId.identifier) }
    }

    @ResetHandler
    fun onReset() {
        colorTagLUTRepository.deleteAll()
    }
}

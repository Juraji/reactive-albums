package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.events.DuplicateLinkedEvent
import nl.juraji.reactive.albums.domain.pictures.events.DuplicateUnlinkedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveDuplicateMatchRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class DuplicateMatchProjectionsEventHandler(
        private val duplicateMatchRepository: ReactiveDuplicateMatchRepository,
) {

    @EventHandler
    fun on(evt: DuplicateLinkedEvent) {
        val entity = DuplicateMatchProjection(
                id = evt.matchId.identifier,
                pictureId = evt.pictureId.identifier,
                targetId = evt.targetId.identifier,
                similarity = (evt.similarity * 100.0).roundToInt()
        )

        duplicateMatchRepository
                .save(entity)
                .block()
    }

    @EventHandler
    fun on(evt: DuplicateUnlinkedEvent) {
        duplicateMatchRepository
                .deleteById(evt.matchId.identifier)
                .block()
    }

    @EventHandler
    fun on(evt: PictureDeletedEvent) {
        duplicateMatchRepository.findAllByPictureId(evt.pictureId.identifier)
                .flatMap { duplicateMatchRepository.delete(it) }
                .blockLast()
    }
}

package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.domain.duplicates.events.DuplicateLinkedEvent
import nl.juraji.reactive.albums.domain.duplicates.events.DuplicateUnlinkedEvent
import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveDuplicateMatchRepository
import org.springframework.stereotype.Service

@Service
class DuplicateMatchProjectionsEventHandler(
        private val duplicateMatchRepository: ReactiveDuplicateMatchRepository,
) {

    fun on(evt: DuplicateLinkedEvent) {
        val entity = DuplicateMatchProjection(
                id = evt.duplicateMatchId.identifier,
                sourceId = evt.sourceId.identifier,
                targetId = evt.targetId.identifier,
                similarity = evt.similarity
        )

        duplicateMatchRepository
                .save(entity)
                .block()
    }

    fun on(evt: DuplicateUnlinkedEvent) {
        duplicateMatchRepository
                .deleteById(evt.duplicateMatchId.identifier)
                .block()
    }
}

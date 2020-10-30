package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.events.DuplicateLinkedEvent
import nl.juraji.reactive.albums.domain.pictures.events.DuplicateUnlinkedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.SyncPictureRepository
import org.axonframework.common.IdentifierFactory
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class DuplicateMatchProjectionsEventHandler(
        private val duplicateMatchRepository: DuplicateMatchRepository,
        private val pictureRepository: SyncPictureRepository,
) {

    @EventHandler
    fun on(evt: DuplicateLinkedEvent) {
        val entity = DuplicateMatchProjection(
                id = IdentifierFactory.getInstance().generateIdentifier(),
                pictureId = evt.pictureId.identifier,
                targetId = evt.targetId.identifier,
                similarity = (evt.similarity * 100.0).roundToInt(),
                pictureDisplayName = pictureRepository.findById(evt.pictureId.identifier).map { it.displayName }.orElse(""),
                targetDisplayName = pictureRepository.findById(evt.targetId.identifier).map { it.displayName }.orElse(""),
        )

        duplicateMatchRepository
                .save(entity)
                .block()
    }

    @EventHandler
    fun on(evt: DuplicateUnlinkedEvent) {
        duplicateMatchRepository
                .deleteByPictureIdAndTargetId(evt.pictureId.identifier, evt.targetId.identifier)
                .block()
    }

    @EventHandler
    fun on(evt: PictureDeletedEvent) {
        duplicateMatchRepository.findAllByPictureId(evt.pictureId.identifier)
                .flatMap { duplicateMatchRepository.delete(it) }
                .blockLast()
    }

    @ResetHandler
    fun onReset() {
        duplicateMatchRepository.getRepository().deleteAll()
    }
}

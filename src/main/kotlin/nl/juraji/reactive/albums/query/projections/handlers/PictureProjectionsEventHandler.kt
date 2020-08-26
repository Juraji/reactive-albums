package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.events.PictureAttributesUpdatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class PictureProjectionsEventHandler(
        private val pictureRepository: PictureRepository,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val queryUpdateEmitter: QueryUpdateEmitter,
) {

    @EventSourcingHandler
    fun on(evt: PictureCreatedEvent) {
        val projection = PictureProjection(
                id = evt.pictureId.identifier,
                displayName = evt.displayName,
                location = evt.location.toString(),
                pictureType = evt.pictureType
        )

        saveAndEmit(projection)
    }

    @EventSourcingHandler
    fun on(evt: PictureAttributesUpdatedEvent) {
        updateAndEmit(evt.pictureId) {
            it.copy(
                    fileSize = evt.fileSize ?: it.fileSize,
                    lastModifiedTime = evt.lastModifiedTime ?: it.lastModifiedTime,
                    imageWidth = evt.imageWidth ?: it.imageWidth,
                    imageHeight = evt.imageHeight ?: it.imageHeight,
                    contentHash = evt.contentHash ?: it.contentHash
            )
        }
    }

    private fun updateAndEmit(id: PictureId, update: (PictureProjection) -> PictureProjection) {
        pictureRepository.findById(id.identifier)
                .map { update(it) }
                .ifPresent { saveAndEmit(it) }
    }

    private fun saveAndEmit(entity: PictureProjection) {
        pictureRepository.runCatching { save(entity) }
                .onSuccess { result -> queryUpdateEmitter.emit({ it.updateResponseType.matches(PictureProjection::class.java) }, result) }
                .onFailure { logger.error("Failed save of ${entity.javaClass.name}", it) }
    }

    fun deleteAndEmit(id: PictureId) {
        pictureRepository.runCatching { deleteById(id.identifier) }
                .onSuccess { queryUpdateEmitter.complete { it.updateResponseType.matches(PictureProjection::class.java) } }
                .onFailure { logger.error("Failed to delete node with id $id: ${it.message}") }
    }

    companion object : LoggerCompanion()
}

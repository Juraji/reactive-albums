package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUpdatedEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Service

@Service
class DirectoryProjectionsEventHandler(
        private val directoryRepository: DirectoryRepository,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val queryUpdateEmitter: QueryUpdateEmitter,
) {

    @EventSourcingHandler
    fun on(evt: DirectoryRegisteredEvent) {
        val entity = DirectoryProjection(
                id = evt.directoryId.identifier,
                location = evt.location.toString(),
                displayName = evt.displayName,
                automaticScanEnabled = evt.automaticScanEnabled
        )

        saveAndEmit(entity)
    }

    @EventSourcingHandler
    fun on(evt: DirectoryUnregisteredEvent) {
        deleteAndEmit(evt.directoryId)
    }

    @EventSourcingHandler
    fun on(evt: DirectoryUpdatedEvent) {
        updateAndEmit(evt.directoryId) {
            it.copy(
                    automaticScanEnabled = evt.automaticScanEnabled
            )
        }
    }

    private fun updateAndEmit(id: DirectoryId, update: (DirectoryProjection) -> DirectoryProjection) {
        directoryRepository.findById(id.identifier)
                .map { update(it) }
                .ifPresent { saveAndEmit(it) }
    }

    private fun saveAndEmit(entity: DirectoryProjection) {
        directoryRepository.runCatching { save(entity) }
                .onSuccess { result -> queryUpdateEmitter.emit({ it.updateResponseType.matches(DirectoryProjection::class.java) }, result) }
                .onFailure { logger.error("Failed save of ${entity.javaClass.name}", it) }
    }

    fun deleteAndEmit(id: DirectoryId) {
        directoryRepository.runCatching { deleteById(id.identifier) }
                .onSuccess { queryUpdateEmitter.complete { it.updateResponseType.matches(DirectoryProjection::class.java) } }
                .onFailure { logger.error("Failed to delete node with id $id: ${it.message}") }
    }

    companion object : LoggerCompanion()
}

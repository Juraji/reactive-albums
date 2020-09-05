package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUpdatedEvent
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveDirectoryRepository
import nl.juraji.reactive.albums.util.LoggerCompanion
import nl.juraji.reactive.albums.util.extensions.logErrors
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class DirectoryProjectionsEventHandler(
        private val directoryRepository: ReactiveDirectoryRepository,
) {

    @EventSourcingHandler
    fun on(evt: DirectoryRegisteredEvent) {
        val entity = DirectoryProjection(
                id = evt.directoryId.identifier,
                location = evt.location.toString(),
                displayName = evt.displayName,
                automaticScanEnabled = evt.automaticScanEnabled
        )

        directoryRepository
                .save(entity)
                .logErrors(logger)
                .subscribe()
    }

    @EventSourcingHandler
    fun on(evt: DirectoryUnregisteredEvent) {
        directoryRepository
                .deleteById(evt.directoryId.identifier)
                .logErrors(logger)
                .subscribe()
    }

    @EventSourcingHandler
    fun on(evt: DirectoryUpdatedEvent) {
        directoryRepository
                .update(evt.directoryId.identifier) {
                    it.copy(automaticScanEnabled = evt.automaticScanEnabled)
                }
                .logErrors(logger)
                .subscribe()
    }

    companion object : LoggerCompanion()
}

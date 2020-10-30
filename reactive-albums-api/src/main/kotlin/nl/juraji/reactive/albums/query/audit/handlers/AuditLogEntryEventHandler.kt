package nl.juraji.reactive.albums.query.audit.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.EntityId
import nl.juraji.reactive.albums.domain.directories.events.DirectoryEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureEvent
import nl.juraji.reactive.albums.domain.tags.events.TagEvent
import nl.juraji.reactive.albums.query.audit.AggregateType
import nl.juraji.reactive.albums.query.audit.AuditLogEntry
import nl.juraji.reactive.albums.query.audit.repositories.AuditLogEntryRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.Timestamp
import org.axonframework.messaging.annotation.MetaDataValue
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

@Service
@ProcessingGroup(ProcessingGroups.AUDIT)
class AuditLogEntryEventHandler(
        private val auditLogEntryRepository: AuditLogEntryRepository,
) {
    @EventHandler
    fun on(
            evt: DirectoryEvent,
            @MetaDataValue("AUDIT") auditLogMessage: String,
            @Timestamp timestamp: Instant,
    ) {
        saveLogEntry(AggregateType.DIRECTORY, evt.directoryId, auditLogMessage, timestamp).block()
    }

    @EventHandler
    fun on(
            evt: PictureEvent,
            @MetaDataValue("AUDIT") auditLogMessage: String,
            @Timestamp timestamp: Instant,
    ) {
        saveLogEntry(AggregateType.PICTURE, evt.pictureId, auditLogMessage, timestamp).block()
    }

    @EventHandler
    fun on(
            evt: TagEvent,
            @MetaDataValue("AUDIT") auditLogMessage: String,
            @Timestamp timestamp: Instant,
    ) {
        saveLogEntry(AggregateType.TAG, evt.tagId, auditLogMessage, timestamp).block()
    }

    @ResetHandler
    fun onReset() {
        auditLogEntryRepository.getRepository().deleteAll()
    }

    private fun saveLogEntry(
            type: AggregateType,
            id: EntityId,
            message: String,
            timestamp: Instant,
    ): Mono<AuditLogEntry> =
            auditLogEntryRepository.save(AuditLogEntry(
                    aggregateType = type,
                    aggregateId = id.toString(),
                    message = message,
                    timestamp = timestamp
            ))
}

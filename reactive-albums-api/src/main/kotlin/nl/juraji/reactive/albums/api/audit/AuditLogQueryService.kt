package nl.juraji.reactive.albums.api.audit

import nl.juraji.reactive.albums.query.audit.AuditLogEntry
import nl.juraji.reactive.albums.query.audit.repositories.AuditLogEntryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class AuditLogQueryService(
        private val auditLogEntryRepository: AuditLogEntryRepository,
) {
    fun findLogEntriesFor(pageable: Pageable, aggregateId: String?): Mono<Page<AuditLogEntry>> =
            Mono.justOrEmpty(aggregateId)
                    .flatMap { auditLogEntryRepository.findByAggregateId(it, pageable) }
                    .switchIfEmpty { auditLogEntryRepository.findAll(pageable) }
}

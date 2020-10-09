package nl.juraji.reactive.albums.query.audit.repositories

import nl.juraji.reactive.albums.query.audit.AuditLogEntry
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

@Repository
interface SyncAuditLogEntryRepository : JpaRepository<AuditLogEntry, Long> {
    fun findByAggregateId(aggregateId: String, pageable: Pageable): Page<AuditLogEntry>
}

@Service
class AuditLogEntryRepository(
        repository: SyncAuditLogEntryRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("auditLogScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncAuditLogEntryRepository, AuditLogEntry, Long>(
        repository,
        scheduler,
        transactionTemplate
) {
    fun findByAggregateId(aggregateId: String, pageable: Pageable): Mono<Page<AuditLogEntry>> =
            from { findByAggregateId(aggregateId, pageable) }
}

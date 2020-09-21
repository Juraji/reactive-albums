package nl.juraji.reactive.albums.query.audit.repositories

import nl.juraji.reactive.albums.query.audit.AggregateType
import nl.juraji.reactive.albums.query.audit.AuditLogEntry
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler

@Repository
interface SyncAuditLogEntryRepository : JpaRepository<AuditLogEntry, Long> {
    fun findByAggregateTypeAndAggregateId(aggregate: AggregateType, aggregateId: String): List<AuditLogEntry>
}

@Service
class AuditLogEntryRepository(
        repository: SyncAuditLogEntryRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncAuditLogEntryRepository, AuditLogEntry, Long>(
        repository,
        scheduler,
        transactionTemplate
) {
    fun findByAggregateTypeAndAggregateId(aggregate: AggregateType, aggregateId: String): Flux<AuditLogEntry> =
            fromIterator { it.findByAggregateTypeAndAggregateId(aggregate, aggregateId) }
}

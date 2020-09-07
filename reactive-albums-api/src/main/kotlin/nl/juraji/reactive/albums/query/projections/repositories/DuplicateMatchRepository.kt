package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

interface DuplicateMatchRepository : JpaRepository<DuplicateMatchProjection, String> {
    fun findBySourceIdOrTargetId(sourceId: String, targetId: String): Optional<DuplicateMatchProjection>
}

@Service
class ReactiveDuplicateMatchRepository(
        duplicateMatchRepository: DuplicateMatchRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<DuplicateMatchRepository, DuplicateMatchProjection, String>(
        duplicateMatchRepository,
        scheduler,
        transactionTemplate
) {
    fun findBySourceIdOrTargetId(sourceId: String, targetId: String): Mono<DuplicateMatchProjection> =
            fromOptional { it.findBySourceIdOrTargetId(sourceId, targetId) }
}

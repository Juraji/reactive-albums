package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.TagProjection
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

@Repository
interface SyncTagRepository : JpaRepository<TagProjection, String> {
    fun existsByLabel(label: String): Boolean
}

@Service
class TagRepository(
        repository: SyncTagRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncTagRepository, TagProjection, String>(
        repository,
        scheduler,
        transactionTemplate
) {
    fun existsByLabel(label: String): Mono<Boolean> =
            from { it.existsByLabel(label) }
}

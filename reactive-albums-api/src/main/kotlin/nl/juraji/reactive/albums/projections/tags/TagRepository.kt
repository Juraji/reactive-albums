package nl.juraji.reactive.albums.projections.tags

import nl.juraji.reactive.albums.projections.ReactiveRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.scheduler.Scheduler

@Repository
interface SyncTagRepository : JpaRepository<TagProjection, String>

@Service
class TagRepository(
        repository: SyncTagRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncTagRepository, TagProjection, String>(
        repository,
        scheduler,
        transactionTemplate
)

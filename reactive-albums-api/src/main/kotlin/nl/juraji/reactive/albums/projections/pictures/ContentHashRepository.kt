package nl.juraji.reactive.albums.projections.pictures

import nl.juraji.reactive.albums.projections.ReactiveRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.scheduler.Scheduler

@Repository
interface SyncContentHashRepository : JpaRepository<ContentHashProjection, String>

@Service
class ContentHashRepository(
        syncContentHashRepository: SyncContentHashRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncContentHashRepository, ContentHashProjection, String>(
        syncContentHashRepository,
        scheduler,
        transactionTemplate
)

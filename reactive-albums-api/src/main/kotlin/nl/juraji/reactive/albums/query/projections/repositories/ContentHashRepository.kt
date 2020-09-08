package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.ContentHashProjection
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.scheduler.Scheduler

@Repository
interface ContentHashRepository : JpaRepository<ContentHashProjection, String>

@Service
class ReactiveContentHashRepository(
        contentHashRepository: ContentHashRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<ContentHashRepository, ContentHashProjection, String>(
        contentHashRepository,
        scheduler,
        transactionTemplate
)

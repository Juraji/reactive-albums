package nl.juraji.reactive.albums.query.thumbnails.repositories

import nl.juraji.reactive.albums.query.projections.repositories.ReactiveRepository
import nl.juraji.reactive.albums.query.thumbnails.Thumbnail
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.scheduler.Scheduler

@Repository
interface ThumbnailRepository : JpaRepository<Thumbnail, String>

@Service
class ReactiveThumbnailRepository(
        thumbnailRepository: ThumbnailRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("thumbnailsScheduler") scheduler: Scheduler,
) : ReactiveRepository<ThumbnailRepository, Thumbnail, String>(
        thumbnailRepository,
        scheduler,
        transactionTemplate
)

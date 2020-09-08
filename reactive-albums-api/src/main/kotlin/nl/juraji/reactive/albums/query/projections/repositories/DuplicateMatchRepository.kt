package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler

interface DuplicateMatchRepository : JpaRepository<DuplicateMatchProjection, String> {
    fun findAllByPictureId(pictureId: String): List<DuplicateMatchProjection>
    fun findAllByTargetId(pictureId: String): List<DuplicateMatchProjection>
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

    fun findAllByPictureId(pictureId: String): Flux<DuplicateMatchProjection> =
            fromIterator { it.findAllByPictureId(pictureId) }

    fun findAllByTargetId(pictureId: String): Flux<DuplicateMatchProjection> =
            fromIterator { it.findAllByTargetId(pictureId) }
}

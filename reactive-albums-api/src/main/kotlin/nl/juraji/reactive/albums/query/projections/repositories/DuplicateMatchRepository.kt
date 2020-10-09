package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

interface SyncDuplicateMatchRepository : JpaRepository<DuplicateMatchProjection, String> {
    fun findAllByPictureId(pictureId: String): List<DuplicateMatchProjection>
    fun findByPictureIdAndTargetId(pictureId: String, targetId: String): Optional<DuplicateMatchProjection>
}

@Service
class DuplicateMatchRepository(
        syncDuplicateMatchRepository: SyncDuplicateMatchRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncDuplicateMatchRepository, DuplicateMatchProjection, String>(
        syncDuplicateMatchRepository,
        scheduler,
        transactionTemplate
) {

    fun findAllByPictureId(pictureId: String): Flux<DuplicateMatchProjection> =
            fromIterator { findAllByPictureId(pictureId) }

    fun deleteByPictureIdAndTargetId(pictureId: String, targetId: String): Mono<DuplicateMatchProjection> =
            fromOptional { findByPictureIdAndTargetId(pictureId, targetId) }
                    .flatMap { delete(it) }

    fun count(): Mono<Long> = from { count() }
}

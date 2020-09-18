package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

interface SyncDuplicateMatchRepository : JpaRepository<DuplicateMatchProjection, String> {
    fun findAllByPictureId(pictureId: String): List<DuplicateMatchProjection>
    fun findAllByTargetId(pictureId: String): List<DuplicateMatchProjection>

    @Query("""
        select om from DuplicateMatchProjection om
        where om.pictureId = (select m1.targetId from DuplicateMatchProjection m1 where m1.id = :matchId)
        and  om.targetId = (select m2.pictureId from DuplicateMatchProjection m2 where m2.id = :matchId)
    """)
    fun findInverseMatchByMatchId(@Param("matchId") matchId: String): Optional<DuplicateMatchProjection>
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
            fromIterator { it.findAllByPictureId(pictureId) }

    fun findAllByTargetId(pictureId: String): Flux<DuplicateMatchProjection> =
            fromIterator { it.findAllByTargetId(pictureId) }

    fun findInverseMatchByMatchId(matchId: String): Mono<DuplicateMatchProjection> =
            fromOptional { it.findInverseMatchByMatchId(matchId) }

    fun count(): Mono<Long> = from { it.count() }
}

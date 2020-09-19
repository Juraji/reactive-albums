package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.PictureProjection
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

@Repository
interface SyncPictureRepository : JpaRepository<PictureProjection, String> {
    fun findAllByDirectoryId(directoryId: String): List<PictureProjection>
    fun findByLocation(path: String): Optional<PictureProjection>
    fun findAllByLocationContainsIgnoreCase(filter: String, pageable: Pageable): Page<PictureProjection>

    @Query("""
        select p from PictureProjection p
        join p.tags t
        where lower(t.tag.label) like lower(concat(:tagLabel, '%')) 
    """)
    fun findAllByTagContainsIgnoreCase(@Param("tagLabel") tagLabel: String, pageable: Pageable): Page<PictureProjection>
}

@Service
class PictureRepository(
        syncPictureRepository: SyncPictureRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncPictureRepository, PictureProjection, String>(
        syncPictureRepository,
        scheduler,
        transactionTemplate
) {

    fun findAllByDirectoryId(directoryId: String): Flux<PictureProjection> = fromIterator { it.findAllByDirectoryId(directoryId) }

    fun findByLocation(path: String): Mono<PictureProjection> = fromOptional { it.findByLocation(path) }

    fun findAllByLocationContainsIgnoreCase(filter: String, pageable: Pageable): Mono<Page<PictureProjection>> =
            from { it.findAllByLocationContainsIgnoreCase(filter, pageable) }

    fun findAllByTagStartsWithIgnoreCase(tagLabel: String, pageable: Pageable): Mono<Page<PictureProjection>> =
            from { it.findAllByTagContainsIgnoreCase(tagLabel, pageable) }
}

package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.query.projections.TagProjection
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

@Repository
interface SyncTagRepository : JpaRepository<TagProjection, String> {
    fun existsByLabel(label: String): Boolean
    fun findByTagTypeAndLabel(tagType: TagType, label: String): Optional<TagProjection>

    @Query(nativeQuery = true, value = """
        select t.* from TagProjection t
        where t.tagType = 'COLOR'
        order by (
            POW((:red - t.tagColorRed) * 0.3, 2) +
            POW((:green - t.tagColorGreen) * 0.59, 2) +
            POW((:blue - t.tagColorBlue) * 0.11, 2)
        )
        limit 1
    """)
    fun findClosestColorTag(
            @Param("red") red: Int,
            @Param("green") green: Int,
            @Param("blue") blue: Int,
    ): TagProjection
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

    fun findClosestColorTag(red: Int, green: Int, blue: Int): Mono<TagProjection> =
            from { it.findClosestColorTag(red, green, blue) }
}

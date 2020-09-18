package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

interface SyncDirectoryRepository : JpaRepository<DirectoryProjection, String> {
    fun existsByLocation(location: String): Boolean
    fun findAllByLocationStartsWith(@Param("parentLocation") parentLocation: String): List<DirectoryProjection>
    fun findAllByAutomaticScanEnabledIsTrue(): List<DirectoryProjection>
}

@Service
class DirectoryRepository(
        syncDirectoryRepository: SyncDirectoryRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<SyncDirectoryRepository, DirectoryProjection, String>(
        syncDirectoryRepository,
        scheduler,
        transactionTemplate
) {

    fun existsByLocation(location: String): Mono<Boolean> =
            from { it.existsByLocation(location) }

    fun findAllByLocationStartsWith(parentLocation: String): Flux<DirectoryProjection> =
            fromIterator { it.findAllByLocationStartsWith(parentLocation) }

    fun findAllByAutomaticScanEnabledIsTrue(): Flux<DirectoryProjection> =
            fromIterator { it.findAllByAutomaticScanEnabledIsTrue() }
}

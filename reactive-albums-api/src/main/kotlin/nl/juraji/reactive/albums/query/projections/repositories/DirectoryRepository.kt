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

interface DirectoryRepository : JpaRepository<DirectoryProjection, String> {
    fun existsByLocation(location: String): Boolean

    fun findAllByLocationStartsWith(@Param("parentLocation") parentLocation: String): List<DirectoryProjection>
}

@Service
class ReactiveDirectoryRepository(
        directoryRepository: DirectoryRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<DirectoryRepository, DirectoryProjection, String>(directoryRepository, scheduler, transactionTemplate) {

    fun existsByLocation(location: String): Mono<Boolean> =
            from { it.existsByLocation(location) }

    fun findAllByLocationStartsWith(parentLocation: String): Flux<DirectoryProjection> =
            fromIterator { it.findAllByLocationStartsWith(parentLocation) }
}

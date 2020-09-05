package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.util.ReactiveRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

@Repository
interface PictureRepository : JpaRepository<PictureProjection, String> {
    fun findPictureImageById(id: String): Optional<PictureProjection>
    fun findPictureThumbnailById(id: String): Optional<PictureProjection>
    fun findAllByDirectoryId(directoryId: String): List<PictureProjection>
}

@Service
class ReactivePictureRepository(
        pictureRepository: PictureRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<PictureRepository, PictureProjection, String>(pictureRepository, scheduler, transactionTemplate) {

    fun findPictureImageById(id: String): Mono<PictureProjection> =
            fromOptional { it.findPictureImageById(id) }

    fun findPictureThumbnailById(id: String): Mono<PictureProjection> =
            fromOptional { it.findPictureThumbnailById(id) }

    fun findAllByDirectoryId(directoryId: String): Flux<PictureProjection> {
        return fromIterator { it.findAllByDirectoryId(directoryId) }
    }
}

package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.PictureImageProjection
import nl.juraji.reactive.albums.query.projections.PictureLocationProjection
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.PictureThumbnailProjection
import nl.juraji.reactive.albums.util.ReactiveRepository
import org.springframework.beans.factory.annotation.Qualifier
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
interface PictureRepository : JpaRepository<PictureProjection, String> {
    fun existsByLocation(location: String): Boolean
    fun findPictureImageById(id: String): Optional<PictureImageProjection>
    fun findPictureThumbnailById(id: String): Optional<PictureThumbnailProjection>

    fun findAllByParentLocation(@Param("directory") directory: String): List<PictureLocationProjection>
}

@Service
class ReactivePictureRepository(
        pictureRepository: PictureRepository,
        transactionTemplate: TransactionTemplate,
        @Qualifier("projectionsScheduler") scheduler: Scheduler,
) : ReactiveRepository<PictureRepository, PictureProjection, String>(pictureRepository, scheduler, transactionTemplate) {

    fun existsByLocation(location: String): Mono<Boolean> =
            from { it.existsByLocation(location) }

    fun findPictureImageById(id: String): Mono<PictureImageProjection> =
            fromOptional { it.findPictureImageById(id) }

    fun findPictureThumbnailById(id: String): Mono<PictureThumbnailProjection> =
            fromOptional { it.findPictureThumbnailById(id) }

    fun findAllByParentLocation(directory: String): Flux<PictureLocationProjection> =
            fromIterator { it.findAllByParentLocation(directory) }
}

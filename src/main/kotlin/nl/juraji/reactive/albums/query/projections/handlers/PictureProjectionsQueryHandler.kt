package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.query.projections.PictureImage
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service

@Service
class PictureProjectionsQueryHandler(
        private val pictureRepository: PictureRepository,
) {

    @QueryHandler
    fun query(q: FindPictureByIdQuery): PictureProjection =
            pictureRepository.findById(q.pictureId.identifier)
                    .orElseThrow { NoSuchEntityException("Picture", q.pictureId) }

    @QueryHandler
    fun queryPictureImage(q: FindPictureByIdQuery): PictureImage =
            pictureRepository.findPictureImageById(q.pictureId.identifier)
                    .orElseThrow { NoSuchEntityException("Picture", q.pictureId) }

    @QueryHandler
    fun query(q: FindAllPicturesQuery): List<PictureProjection> =
            pictureRepository.findAll()
}

/**
 * Possible results:
 * - PictureProjection::class.java
 * - PictureImage::class.java
 */
data class FindPictureByIdQuery(
        val pictureId: PictureId,
)

/**
 * Possible results:
 * - ResponseTypes.multipleInstancesOf(PictureProjection::class)
 */
class FindAllPicturesQuery

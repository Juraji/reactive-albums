package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.handlers.NoSuchEntityException
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.query.thumbnails.Thumbnail
import nl.juraji.reactive.albums.query.thumbnails.repositories.ReactiveThumbnailRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class PictureImageService(
        private val pictureRepository: PictureRepository,
        private val thumbnailRepository: ReactiveThumbnailRepository,
) {

    fun getThumbnail(pictureId: String): Mono<Thumbnail> = thumbnailRepository
            .findById(pictureId)
            .switchIfEmpty { Mono.error(NoSuchEntityException("Thumbnail", pictureId)) }

    fun getPictureLocation(pictureId: String): Mono<String> = pictureRepository
            .findById(pictureId)
            .map { it.location }
            .switchIfEmpty { Mono.error(NoSuchEntityException("Picture", pictureId)) }
}

package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.handlers.NoSuchEntityException
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.query.thumbnails.repositories.ReactiveThumbnailRepository
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration


@RestController
class PictureImageController(
        private val pictureRepository: PictureRepository,
        private val thumbnailRepository: ReactiveThumbnailRepository,
) {

    @GetMapping("/api/pictures/{pictureId}/thumbnail")
    fun getPictureThumbnail(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<ResponseEntity<Resource>> = thumbnailRepository
            .findById(pictureId)
            .switchIfEmpty { Mono.error { NoSuchEntityException("Thumbnail", pictureId) } }
            .map {
                ResponseEntity.ok()
                        .cacheControl(thumbnailCacheControl)
                        .body(ByteArrayResource(it.thumbnail))
            }

    @GetMapping("/api/pictures/{pictureId}/image")
    fun getPictures(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<Resource> = pictureRepository
            .findById(pictureId)
            .switchIfEmpty { Mono.error(NoSuchEntityException("Picture", pictureId)) }
            .map { FileSystemResource(it.location) }

    companion object{
        val thumbnailCacheControl = CacheControl
                .maxAge(Duration.ofDays(1))
    }
}

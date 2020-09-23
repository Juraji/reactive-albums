package nl.juraji.reactive.albums.api.pictures

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration


@RestController
class PictureImageController(
        private val pictureImageService: PictureImageService
) {

    @GetMapping("/api/pictures/{pictureId}/thumbnail")
    fun getPictureThumbnail(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<ResponseEntity<Resource>> = pictureImageService
            .getThumbnail(pictureId)
            .map {
                ResponseEntity.ok()
                        .cacheControl(thumbnailCacheControl)
                        .body(ByteArrayResource(it))
            }

    @GetMapping("/api/pictures/{pictureId}/image")
    fun getPictures(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<Resource> = pictureImageService
            .getPictureLocation(pictureId)
            .map { FileSystemResource(it) }

    companion object{
        val thumbnailCacheControl = CacheControl
                .maxAge(Duration.ofDays(1))
    }
}

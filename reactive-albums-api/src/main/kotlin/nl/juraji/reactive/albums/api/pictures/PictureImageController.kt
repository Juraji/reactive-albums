package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.handlers.NoSuchEntityException
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ZeroCopyHttpOutputMessage
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.io.File
import java.time.Duration


@RestController
class PictureImageController(
        private val pictureRepository: ReactivePictureRepository,
) {

    @GetMapping("/api/pictures/{pictureId}/thumbnail")
    fun getPictureThumbnail(
            @PathVariable("pictureId") pictureId: String,
            response: ServerHttpResponse,
    ): Mono<Void> {
        return pictureRepository
                .findPictureThumbnailById(pictureId)
                .filter { it.thumbnailLocation != null && it.thumbnailType != null }
                .switchIfEmpty { NoSuchEntityException("Picture", pictureId).toMono() }
                .flatMap {
                    writeFile(
                            response = response,
                            path = it.thumbnailLocation!!,
                            mediaType = it.thumbnailType!!.mediaType,
                    )
                }
    }

    @GetMapping("/api/pictures/{pictureId}/image")
    fun getPictures(
            @PathVariable("pictureId") pictureId: String,
            response: ServerHttpResponse,
    ): Mono<Void> = pictureRepository
            .findPictureImageById(pictureId)
            .switchIfEmpty { NoSuchEntityException("Picture", pictureId).toMono() }
            .flatMap {
                writeFile(
                        response = response,
                        path = it.location,
                        mediaType = it.pictureType.mediaType
                )
            }

    private fun writeFile(response: ServerHttpResponse, path: String, mediaType: MediaType): Mono<Void> {
        val zeroCopyResponse = response as ZeroCopyHttpOutputMessage
        response.getHeaders().contentType = mediaType
        response.getHeaders().cacheControl = CacheControl
                .maxAge(Duration.ofDays(7))
                .cachePrivate()
                .headerValue

        val file = File(path)
        return zeroCopyResponse.writeWith(file, 0, file.length())
    }
}

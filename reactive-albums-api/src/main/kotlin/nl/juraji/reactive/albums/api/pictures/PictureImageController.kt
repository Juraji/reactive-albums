package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.handlers.NoSuchEntityException
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty


@RestController
class PictureImageController(
        private val pictureRepository: ReactivePictureRepository,
) {

    @GetMapping("/api/pictures/{pictureId}/thumbnail")
    fun getPictureThumbnail(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<Resource> = pictureRepository
            .findPictureThumbnailById(pictureId)
            .filter { it.thumbnailLocation != null }
            .map { FileSystemResource(it.thumbnailLocation!!) as Resource }
            .switchIfEmpty { Mono.just(ClassPathResource("/thumbnail-placeholder.gif") as Resource) }

    @GetMapping("/api/pictures/{pictureId}/image")
    fun getPictures(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<Resource> = pictureRepository
            .findPictureImageById(pictureId)
            .switchIfEmpty { Mono.error(NoSuchEntityException("Picture", pictureId)) }
            .map { FileSystemResource(it.location) }
}

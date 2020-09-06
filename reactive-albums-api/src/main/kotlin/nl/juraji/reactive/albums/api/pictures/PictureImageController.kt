package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.handlers.NoSuchEntityException
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class PictureImageController(
        private val pictureRepository: ReactivePictureRepository,
) {

    @GetMapping("/api/pictures/{pictureId}/thumbnail")
    fun getPictureThumbnail(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<Resource> {
        return pictureRepository
                .findPictureThumbnailById(pictureId)
                .filter { it.thumbnailLocation != null }
                .switchIfEmpty(Mono.error(NoSuchEntityException("Picture", pictureId)))
                .map { FileSystemResource(it.thumbnailLocation!!) }
    }

    @GetMapping("/api/pictures/{pictureId}/image")
    fun getPictures(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<Resource> = pictureRepository
            .findPictureImageById(pictureId)
            .switchIfEmpty(Mono.error(NoSuchEntityException("Picture", pictureId)))
            .map { FileSystemResource(it.location) }
}

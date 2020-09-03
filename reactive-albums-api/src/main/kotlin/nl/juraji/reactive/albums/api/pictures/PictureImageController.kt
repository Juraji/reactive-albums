package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.configuration.PicturesAggregateConfiguration
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.nio.file.Path
import java.nio.file.Paths

@RestController
class PictureImageController(
        private val pictureRepository: ReactivePictureRepository,
        private val picturesConfiguration: PicturesAggregateConfiguration,
) {

    @GetMapping("/api/pictures/{pictureId}/thumbnail")
    fun getPictureThumbnail(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<FileSystemResource> {
        val thumbnailMT = picturesConfiguration.thumbnailMimeType
        return Mono
                .from<Path> { Paths.get(picturesConfiguration.thumbnailLocation, "$pictureId.${thumbnailMT.subtype}") }
                .map { FileSystemResource(it) }
    }

    @GetMapping("/api/pictures/{pictureId}/image")
    fun getPictures(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<Resource> = pictureRepository
            .findPictureImageById(pictureId)
            .map { FileSystemResource(it.location) }
}

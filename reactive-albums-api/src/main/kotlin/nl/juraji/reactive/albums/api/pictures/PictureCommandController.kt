package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.PictureProjection
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class PictureCommandController(
        private val picturesService: PicturesService,
) {

    @PostMapping("/api/pictures")
    fun addPicture(@RequestBody dto: PictureDto): Mono<PictureProjection> =
            picturesService.addPicture(
                    location = dto.location,
                    displayName = dto.displayName
            )
}

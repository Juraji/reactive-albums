package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveDuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class PictureQueryController(
        private val pictureRepository: ReactivePictureRepository,
        private val duplicateMatchRepository: ReactiveDuplicateMatchRepository,
) {

    @GetMapping("/api/pictures")
    fun getPictures(): Flux<PictureProjection> = pictureRepository.findAll()

    @GetMapping("/api/duplicate-matches")
    fun getPictureDuplicates(): Flux<DuplicateMatchProjection> = duplicateMatchRepository.findAll()
}

package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.configuration.PaginationDefaults.DEFAULT_PAGE_NO
import nl.juraji.reactive.albums.configuration.PaginationDefaults.DEFAULT_PAGE_SIZE
import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class PictureQueryController(
        private val pictureRepository: PictureRepository,
        private val duplicateMatchRepository: DuplicateMatchRepository,
) {

    @GetMapping("/api/pictures")
    fun getPictures(
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE_NO) page: Int,
            @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) size: Int,
            @RequestParam(name = "filter", required = false) filter: String?,
    ): Mono<Page<PictureProjection>> = if (filter.isNullOrBlank()) {
        pictureRepository.findAll(PageRequest.of(page, size))
    } else {
        pictureRepository.findAllByLocationContainsIgnoreCase(filter, PageRequest.of(page, size))
    }

    @GetMapping("/api/pictures/{pictureId}")
    fun getPicture(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<PictureProjection> = pictureRepository.findById(pictureId)

    @GetMapping("/api/pictures/{pictureId}/duplicate-matches")
    fun getPictureDuplicateMatches(
            @PathVariable("pictureId") pictureId: String,
    ): Flux<DuplicateMatchProjection> = duplicateMatchRepository.findAllByPictureId(pictureId)

    @GetMapping("/api/duplicate-matches-count")
    fun getPictureDuplicates(): Mono<Long> = duplicateMatchRepository.count()
}

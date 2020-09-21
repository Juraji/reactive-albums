package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.configuration.PaginationDefaults.DEFAULT_PAGE_NO
import nl.juraji.reactive.albums.configuration.PaginationDefaults.DEFAULT_PAGE_SIZE
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class PictureQueryController(
        private val pictureRepository: PictureRepository,
) {

    @GetMapping("/api/pictures")
    fun getPictures(
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE_NO) page: Int,
            @RequestParam(name = "size", defaultValue = DEFAULT_PAGE_SIZE) size: Int,
            @RequestParam(name = "sort", defaultValue = "displayName,desc") sort: Sort,
            @RequestParam(name = "filter", required = false) filter: String?,
    ): Mono<Page<PictureProjection>> {
        val pageable: Pageable = PageRequest.of(page, size, sort)
        return when {
            filter.isNullOrBlank() -> pictureRepository.findAll(pageable)
            filter.startsWith("tag:") -> pictureRepository.findAllByTagStartsWithIgnoreCase(filter.substring(4), pageable)
            else -> pictureRepository.findAllByLocationContainsIgnoreCase(filter, pageable)
        }
    }

    @GetMapping("/api/pictures/{pictureId}")
    fun getPicture(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<PictureProjection> = pictureRepository.findById(pictureId)
}

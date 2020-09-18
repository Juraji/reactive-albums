package nl.juraji.reactive.albums.api.tags

import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.query.projections.repositories.TagRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class TagQueryController(
        private val tagRepository: TagRepository,
) {

    @GetMapping("/api/tags")
    fun getTags(): Flux<TagProjection> = tagRepository.findAll()
}

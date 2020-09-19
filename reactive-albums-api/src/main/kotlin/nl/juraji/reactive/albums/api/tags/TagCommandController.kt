package nl.juraji.reactive.albums.api.tags

import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.services.TagsService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
class TagCommandController(
        private val tagsService: TagsService,
) {

    @PostMapping("/api/tags")
    fun createTag(
            @RequestParam("label") label: String,
            @RequestParam("tagColor", required = false) tagColor: String?,
            @RequestParam("textColor", required = false) textColor: String?,
    ): Mono<TagProjection> = tagsService.createTag(label, tagColor, textColor)

    @PutMapping("/api/tags/{tagId}")
    fun updateTag(
            @PathVariable("tagId") tagId: String,
            @RequestParam("label", required = false) label: String?,
            @RequestParam("tagColor", required = false) tagColor: String?,
            @RequestParam("textColor", required = false) textColor: String?,
    ): Mono<TagProjection> = tagsService.updateTag(tagId, label, tagColor, textColor)

    @DeleteMapping("/api/tags/{tagId}")
    fun deleteTag(
            @PathVariable("tagId") tagId: String,
    ): Mono<String> = tagsService.deleteTag(tagId)
}

package nl.juraji.reactive.albums.api.tags

import nl.juraji.reactive.albums.query.projections.TagProjection
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
class TagCommandController(
        private val tagsService: TagsService,
) {

    @PostMapping("/api/tags")
    fun createTag(
            @RequestBody tag: CreateTagDto,
    ): Mono<TagProjection> = tagsService.createTag(tag.label, tag.tagColor, tag.textColor)

    @PutMapping("/api/tags/{tagId}")
    fun updateTag(
            @PathVariable("tagId") tagId: String,
            @RequestBody tag: UpdateTagDto,
    ): Mono<TagProjection> = tagsService.updateTag(tagId, tag.label, tag.tagColor, tag.textColor)

    @DeleteMapping("/api/tags/{tagId}")
    fun deleteTag(
            @PathVariable("tagId") tagId: String,
    ): Mono<String> = tagsService.deleteTag(tagId)
}

package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.TagLink
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class PictureCommandController(
        private val pictureCommandsService: PictureCommandsService,
) {

    @PostMapping("/api/pictures/{pictureId}/rescan-duplicates")
    fun rescanDuplicates(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<String> = pictureCommandsService
            .rescanDuplicates(pictureId = pictureId)
            .map { it.identifier }

    @DeleteMapping("/api/pictures/{pictureId}/duplicate-matches/{matchId}")
    fun unlinkDuplicateMatch(
            @PathVariable("pictureId") pictureId: String,
            @PathVariable("matchId") matchId: String,
    ): Mono<Void> = pictureCommandsService.unlinkDuplicateMatch(
            pictureId = pictureId,
            matchId = matchId
    )

    @PostMapping("/api/pictures/{pictureId}/move")
    fun movePicture(
            @PathVariable("pictureId") pictureId: String,
            @RequestParam("targetDirectoryId") targetDirectoryId: String,
    ): Mono<PictureProjection> = pictureCommandsService.movePicture(pictureId, targetDirectoryId)

    @DeleteMapping("/api/pictures/{pictureId}")
    fun deletePicture(
            @PathVariable("pictureId") pictureId: String,
            @RequestParam("deletePhysicalFile", required = false, defaultValue = "false") deletePhysicalFile: Boolean,
    ): Mono<String> = pictureCommandsService
            .deletePicture(pictureId = pictureId, deletePhysicalFile = deletePhysicalFile)
            .map { it.identifier }

    @PostMapping("/api/pictures/{pictureId}/tags/{tagId}")
    fun linkTag(
            @PathVariable("pictureId") pictureId: String,
            @PathVariable("tagId") tagId: String,
    ): Flux<TagLink> = pictureCommandsService.linkTag(pictureId, tagId)

    @DeleteMapping("/api/pictures/{pictureId}/tags/{tagId}")
    fun unlinkTag(
            @PathVariable("pictureId") pictureId: String,
            @PathVariable("tagId") tagId: String,
    ): Flux<TagLink> = pictureCommandsService.unlinkTag(pictureId, tagId)
}

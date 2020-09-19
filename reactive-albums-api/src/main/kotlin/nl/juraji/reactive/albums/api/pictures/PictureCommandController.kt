package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.services.PicturesService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
class PictureCommandController(
        private val picturesService: PicturesService,
) {

    @PostMapping("/api/pictures/{pictureId}/rescan-duplicates")
    fun rescanDuplicates(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<PictureId> = picturesService.rescanDuplicates(pictureId = pictureId)

    @DeleteMapping("/api/pictures/{pictureId}/duplicate-matches/{matchId}")
    fun unlinkDuplicateMatch(
            @PathVariable("pictureId") pictureId: String,
            @PathVariable("matchId") matchId: String,
    ): Mono<Void> = picturesService.unlinkDuplicateMatch(
            pictureId = pictureId,
            matchId = matchId
    )

    @DeleteMapping("/api/pictures/{pictureId}")
    fun deletePicture(
            @PathVariable("pictureId") pictureId: String,
            @RequestParam("deletePhysicalFile", required = false, defaultValue = "false") deletePhysicalFile: Boolean,
    ): Mono<String> = picturesService.deletePicture(pictureId = pictureId, deletePhysicalFile = deletePhysicalFile)

    @PostMapping("/api/pictures/{pictureId}/tags/{tagId}")
    fun linkTag(
            @PathVariable("pictureId") pictureId: String,
            @PathVariable("tagId") tagId: String,
    ): Mono<Void> = picturesService.linkTag(pictureId, tagId)

    @DeleteMapping("/api/pictures/{pictureId}/tags/{tagId}")
    fun unlinkTag(
            @PathVariable("pictureId") pictureId: String,
            @PathVariable("tagId") tagId: String,
    ): Mono<Void> = picturesService.unlinkTag(pictureId, tagId)
}

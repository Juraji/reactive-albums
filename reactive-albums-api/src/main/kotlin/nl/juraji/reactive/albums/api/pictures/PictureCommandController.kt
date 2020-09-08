package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class PictureCommandController(
        private val picturesService: PicturesService,
) {

    @PostMapping("/api/pictures/{pictureId}/rescan-duplicates")
    fun rescanDuplicates(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<Void> =
            picturesService.rescanDuplicates(pictureId = PictureId(pictureId))

    @PostMapping("/api/pictures/{pictureId}/unlink-duplicate-match/{matchId}")
    fun unlinkDuplicateMatch(
            @PathVariable("pictureId") pictureId: String,
            @PathVariable("matchId") matchId: String,
    ): Mono<Void> =
            picturesService.unlinkDuplicateMatch(
                    pictureId = PictureId(pictureId),
                    matchId = DuplicateMatchId(matchId)
            )

    @DeleteMapping("/api/pictures/{pictureId}/rescan-duplicates")
    fun deletePicture(
            @PathVariable("pictureId") pictureId: String,
    ): Mono<Void> =
            picturesService.deletePicture(pictureId = PictureId(pictureId))
}

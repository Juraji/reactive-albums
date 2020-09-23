package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveEvent
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PictureEventsController(
        private val pictureEventsService: PictureEventsService
) {

    @GetMapping("/api/events/duplicate-match-count")
    fun getDuplicateMatchCount(): ServerSentEventFlux<Long> = pictureEventsService.getDuplicateMatchCountStream()


    @GetMapping("/api/events/duplicate-matches")
    fun getAllDuplicateMatches(): ServerSentEventFlux<ReactiveEvent<DuplicateMatchProjection>> =pictureEventsService.getAllDuplicateMatchesStream()

    @GetMapping("/api/events/duplicate-matches/{pictureId}")
    fun getPictureDuplicateMatches(
            @PathVariable("pictureId") pictureId: String,
    ): ServerSentEventFlux<ReactiveEvent<DuplicateMatchProjection>> = pictureEventsService.getDuplicateMatchStreamByPictureId(pictureId)
}

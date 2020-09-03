package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import nl.juraji.reactive.albums.util.ReactiveEvent
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import nl.juraji.reactive.albums.util.extensions.bufferLastIdentity
import nl.juraji.reactive.albums.util.extensions.toServerSentEvents
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration

@RestController
class PictureQueryController(
        private val pictureRepository: ReactivePictureRepository,
) {

    @GetMapping("/api/pictures")
    fun getPictures(): Flux<PictureProjection> = pictureRepository.findAll()

    @GetMapping("/api/pictures/updates", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getPictureUpdates(): ServerSentEventFlux<List<ReactiveEvent<PictureProjection>>> =
            pictureRepository
                    .subscribeToAll()
                    .bufferLastIdentity(Duration.ofMillis(1500)) { it.entity.id }
                    .toServerSentEvents()
}

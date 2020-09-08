package nl.juraji.reactive.albums.api

import nl.juraji.reactive.albums.query.projections.repositories.ReactiveDirectoryRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveDuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveEvent
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import nl.juraji.reactive.albums.util.extensions.bufferLastIdentity
import nl.juraji.reactive.albums.util.extensions.toServerSentEvents
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration

@RestController
class ProjectionEventsController(
        private val directoryRepository: ReactiveDirectoryRepository,
        private val pictureRepository: ReactivePictureRepository,
        private val duplicateMatchRepository: ReactiveDuplicateMatchRepository,
) {

    @GetMapping("/api/events")
    fun subscribeToAllEvents(): ServerSentEventFlux<List<ReactiveEvent<out Any>>> {
        return Flux.merge(
                directoryRepository.subscribeToAll().bufferLastIdentity(bufferTime) { it.entity.id },
                pictureRepository.subscribeToAll().bufferLastIdentity(bufferTime) { it.entity.id },
                duplicateMatchRepository.subscribeToAll().bufferLastIdentity(bufferTime) { it.entity.id }
        ).toServerSentEvents()
    }

    companion object {
        private val bufferTime: Duration = Duration.ofMillis(1500)
    }
}

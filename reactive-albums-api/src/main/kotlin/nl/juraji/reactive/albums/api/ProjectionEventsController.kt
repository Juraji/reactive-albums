package nl.juraji.reactive.albums.api

import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveEvent
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import nl.juraji.reactive.albums.util.extensions.bufferLastIdentity
import nl.juraji.reactive.albums.util.extensions.toServerSentEvents
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration

@RestController
class ProjectionEventsController(
        private val directoryRepository: DirectoryRepository,
        private val pictureRepository: PictureRepository,
        private val duplicateMatchRepository: DuplicateMatchRepository,
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

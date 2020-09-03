package nl.juraji.reactive.albums.api.directories

import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveDirectoryRepository
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
class DirectoriesQueryController(
        private val directoryRepository: ReactiveDirectoryRepository,
) {

    @GetMapping("/api/directories")
    fun getAllDirectories(): Flux<DirectoryProjection> =
            directoryRepository.findAll()

    @GetMapping("/api/directories/updates", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getDirectoryUpdates(): ServerSentEventFlux<List<ReactiveEvent<DirectoryProjection>>> =
            directoryRepository
                    .subscribeToAll()
                    .bufferLastIdentity(Duration.ofMillis(1500)) { it.entity.id }
                    .toServerSentEvents()
}

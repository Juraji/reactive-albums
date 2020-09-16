package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import nl.juraji.reactive.albums.util.extensions.toServerSentEvents
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class PictureEventsController(
        private val duplicateMatchRepository: DuplicateMatchRepository,
) {

    @GetMapping("/api/events/duplicate-match-count")
    fun getDuplicateMatchCount(): ServerSentEventFlux<Long> =
            Flux.concat(
                    duplicateMatchRepository.count(),
                    duplicateMatchRepository.subscribeToAll()
                            .flatMap { duplicateMatchRepository.count() }
                            .distinct()
            ).toServerSentEvents()
}

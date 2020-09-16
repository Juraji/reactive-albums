package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.DuplicateMatchView
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.EventType
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveEvent
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import nl.juraji.reactive.albums.util.extensions.toServerSentEvents
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class PictureEventsController(
        private val pictureRepository: PictureRepository,
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


    @GetMapping("/api/events/duplicate-matches/{pictureId}")
    fun getPictureDuplicateMatches(
            @PathVariable("pictureId") pictureId: String,
    ): ServerSentEventFlux<ReactiveEvent<out Any>> {
        val matchToView: (DuplicateMatchProjection) -> Mono<DuplicateMatchView> = { match ->
            pictureRepository.findById(match.targetId).map { target ->
                DuplicateMatchView(
                        id = match.id,
                        pictureId = match.pictureId,
                        targetId = match.targetId,
                        similarity = match.similarity,
                        target = target,
                )
            }
        }

        return Flux.merge(
                duplicateMatchRepository.findAllByPictureId(pictureId).flatMap(matchToView).map { ReactiveEvent.of(EventType.UPDATE, it) },
                duplicateMatchRepository.subscribe { it.pictureId == pictureId }
                        .flatMap { evt ->
                            if (evt.type == EventType.DELETE) Mono.just(evt)
                            else matchToView(evt.entity).map { ReactiveEvent.of(evt.type, it) }
                        }
        )
                .toServerSentEvents()
    }
}

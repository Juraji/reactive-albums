package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
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
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

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


    @GetMapping("/api/events/duplicate-matches")
    fun getAllDuplicateMatches(): ServerSentEventFlux<ReactiveEvent<DuplicateMatchProjection>> {
        val fetchPictures: (DuplicateMatchProjection) -> Mono<DuplicateMatchProjection> = { match ->
            Mono.zip(
                    pictureRepository.findById(match.pictureId),
                    pictureRepository.findById(match.targetId)
            ).map { (picture, target) ->
                val copy = match.copy(
                        picture = picture,
                        target = target
                )

                copy
            }
        }

        return Flux.merge(
                duplicateMatchRepository.findAll().flatMap(fetchPictures).map { ReactiveEvent(EventType.UPSERT, it) },
                duplicateMatchRepository.subscribeToAll().flatMap { evt ->
                    if (evt.type == EventType.DELETE) Mono.just(evt)
                    else fetchPictures(evt.entity).map { ReactiveEvent(evt.type, it) }
                }
        ).toServerSentEvents()
    }

    @GetMapping("/api/events/duplicate-matches/{pictureId}")
    fun getPictureDuplicateMatches(
            @PathVariable("pictureId") pictureId: String,
    ): ServerSentEventFlux<ReactiveEvent<DuplicateMatchProjection>> {
        val fetchPicture: (DuplicateMatchProjection) -> Mono<DuplicateMatchProjection> = { match ->
            pictureRepository.findById(match.targetId).map { target ->
                match.copy(target = target)
            }
        }

        return Flux.merge(
                duplicateMatchRepository.findAllByPictureId(pictureId).flatMap(fetchPicture).map { ReactiveEvent(EventType.UPSERT, it) },
                duplicateMatchRepository.subscribe { it.pictureId == pictureId }
                        .flatMap { evt ->
                            if (evt.type == EventType.DELETE) Mono.just(evt)
                            else fetchPicture(evt.entity).map { ReactiveEvent(evt.type, it) }
                        }
        ).toServerSentEvents()
    }
}

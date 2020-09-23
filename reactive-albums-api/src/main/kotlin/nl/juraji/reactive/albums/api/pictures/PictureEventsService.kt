package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.EventType
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveEvent
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import nl.juraji.reactive.albums.util.extensions.toServerSentEvents
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class PictureEventsService(
        private val pictureRepository: PictureRepository,
        private val duplicateMatchRepository: DuplicateMatchRepository,
) {
    fun getDuplicateMatchCountStream(): ServerSentEventFlux<Long> = Flux
            .concat(
                    duplicateMatchRepository.count(),
                    duplicateMatchRepository.subscribeToAll()
                            .flatMap { duplicateMatchRepository.count() }
                            .distinct()
            )
            .toServerSentEvents()

    fun getAllDuplicateMatchesStream(): ServerSentEventFlux<ReactiveEvent<DuplicateMatchProjection>> {
        val fetchPictures: (DuplicateMatchProjection) -> Mono<DuplicateMatchProjection> = { match ->
            Mono.zip(
                    pictureRepository.findById(match.pictureId),
                    pictureRepository.findById(match.targetId)
            ).map { (picture, target) ->
                match.copy(picture = picture, target = target)
            }
        }

        val initialStream: Flux<ReactiveEvent<DuplicateMatchProjection>> = duplicateMatchRepository
                .findAll()
                .flatMap(fetchPictures)
                .map { ReactiveEvent(EventType.UPSERT, it) }

        val updateStream: Flux<ReactiveEvent<DuplicateMatchProjection>> = duplicateMatchRepository
                .subscribeToAll()
                .flatMap { evt ->
                    if (evt.type == EventType.DELETE) Mono.just(evt)
                    else fetchPictures(evt.entity).map { ReactiveEvent(evt.type, it) }
                }

        return Flux.merge(initialStream, updateStream).toServerSentEvents()
    }

    fun getDuplicateMatchStreamByPictureId(pictureId: String): ServerSentEventFlux<ReactiveEvent<DuplicateMatchProjection>> {
        val fetchPicture: (DuplicateMatchProjection) -> Mono<DuplicateMatchProjection> = { match ->
            pictureRepository.findById(match.targetId).map { target ->
                match.copy(target = target)
            }
        }

        val initialStream: Flux<ReactiveEvent<DuplicateMatchProjection>> = duplicateMatchRepository
                .findAllByPictureId(pictureId)
                .flatMap(fetchPicture)
                .map { ReactiveEvent(EventType.UPSERT, it) }

        val updateStream = duplicateMatchRepository
                .subscribe { it.pictureId == pictureId }
                .flatMap { evt ->
                    if (evt.type == EventType.DELETE) Mono.just(evt)
                    else fetchPicture(evt.entity).map { ReactiveEvent(evt.type, it) }
                }

        return Flux.merge(initialStream, updateStream).toServerSentEvents()
    }

}

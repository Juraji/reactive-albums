package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.EventType
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveEvent
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import nl.juraji.reactive.albums.util.extensions.toServerSentEvents
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class PictureEventsService(
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
        val initialStream: Flux<ReactiveEvent<DuplicateMatchProjection>> = duplicateMatchRepository
                .findAll()
                .map { ReactiveEvent(EventType.UPSERT, it) }

        val updateStream: Flux<ReactiveEvent<DuplicateMatchProjection>> = duplicateMatchRepository
                .subscribeToAll()

        return Flux.merge(initialStream, updateStream).toServerSentEvents()
    }

    fun getDuplicateMatchStreamByPictureId(pictureId: String): ServerSentEventFlux<ReactiveEvent<DuplicateMatchProjection>> {

        val initialStream: Flux<ReactiveEvent<DuplicateMatchProjection>> = duplicateMatchRepository
                .findAllByPictureId(pictureId)
                .map { ReactiveEvent(EventType.UPSERT, it) }

        val updateStream = duplicateMatchRepository
                .subscribe { it.pictureId == pictureId }

        return Flux.merge(initialStream, updateStream).toServerSentEvents()
    }

}

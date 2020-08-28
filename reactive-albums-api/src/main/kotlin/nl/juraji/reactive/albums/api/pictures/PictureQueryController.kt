package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.handlers.FindAllPicturesQuery
import nl.juraji.reactive.albums.services.SseStreamService
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class PictureQueryController(
        private val queryGateway: QueryGateway,
        private val sseStreamService: SseStreamService,
) {

    @GetMapping("/api/pictures", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getPictures(): Flux<ServerSentEvent<PictureProjection>> {
        val query = queryGateway.subscriptionQuery(
                FindAllPicturesQuery(),
                ResponseTypes.multipleInstancesOf(PictureProjection::class.java),
                ResponseTypes.instanceOf(PictureProjection::class.java)
        )

        return sseStreamService.asEventStream(
                Flux.concat(
                        query.initialResult().flatMapMany { Flux.fromIterable(it) },
                        query.updates()
                )
        )
    }
}

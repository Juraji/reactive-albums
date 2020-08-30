package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.api.ServerSentEventFlux
import nl.juraji.reactive.albums.api.SseController
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.handlers.FindAllPicturesQuery
import nl.juraji.reactive.albums.util.extensions.bufferLastIdentity
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@RestController
class PictureQueryController(
        private val queryGateway: QueryGateway,
) : SseController() {

    @GetMapping("/api/pictures")
    fun getPictures(): Mono<List<PictureProjection>> {
        val query = queryGateway.query(
                FindAllPicturesQuery(),
                ResponseTypes.multipleInstancesOf(PictureProjection::class.java),
        )

        return query.toMono()
    }

    @GetMapping("/api/pictures/updates", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getPictureUpdates(): ServerSentEventFlux<List<PictureProjection>> {
        val updates: Flux<List<PictureProjection>> = queryGateway.subscriptionQuery(FindAllPicturesQuery(), PictureProjection::class.java, PictureProjection::class.java)
                .updates()
                .bufferLastIdentity(Duration.ofMillis(1500)) { it.id }


        return asEventStream(updates)
    }
}

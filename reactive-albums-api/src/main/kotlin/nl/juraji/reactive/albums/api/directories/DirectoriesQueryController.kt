package nl.juraji.reactive.albums.api.directories

import nl.juraji.reactive.albums.api.ServerSentEventFlux
import nl.juraji.reactive.albums.api.SseController
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.handlers.FindAllDirectoriesQuery
import nl.juraji.reactive.albums.query.projections.handlers.FindAllPicturesQuery
import nl.juraji.reactive.albums.util.extensions.bufferLastIdentity
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@RestController
class DirectoriesQueryController(
        private val queryGateway: QueryGateway,
) : SseController() {

    @GetMapping("/api/directories")
    fun getAllDirectories(): Flux<DirectoryProjection> = queryGateway
            .query(FindAllDirectoriesQuery(), ResponseTypes.multipleInstancesOf(DirectoryProjection::class.java))
            .toMono()
            .flatMapMany { Flux.fromIterable(it) }

    @GetMapping("/api/directories/updates", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getDirectoryUpdates(): ServerSentEventFlux<List<DirectoryProjection>> {
        val updates: Flux<List<DirectoryProjection>> = queryGateway.subscriptionQuery(FindAllPicturesQuery(), DirectoryProjection::class.java, DirectoryProjection::class.java)
                .updates()
                .bufferLastIdentity(Duration.ofMillis(1500)) { it.id }


        return asEventStream(updates)
    }
}

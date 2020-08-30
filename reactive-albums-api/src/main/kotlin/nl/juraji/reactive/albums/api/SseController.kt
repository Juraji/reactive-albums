package nl.juraji.reactive.albums.api

import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

typealias ServerSentEventFlux<T> = Flux<ServerSentEvent<T?>>

abstract class SseController {

    protected fun <T> asEventStream(source: Flux<T>): ServerSentEventFlux<T> {
        val sourceStream: ServerSentEventFlux<T> = source.map {
            ServerSentEvent
                    .builder(it)
                    .id(UUID.randomUUID().toString())
                    .build()
        }

        val heartbeatStream: ServerSentEventFlux<T> = Flux
                .interval(heartbeatDelay, heartbeatInterval)
                .map {
                    ServerSentEvent
                            .builder<T>()
                            .event("ping")
                            .build()
                }

        return Flux.merge(sourceStream, heartbeatStream)
    }

    companion object {
        private val heartbeatDelay = Duration.ZERO
        private val heartbeatInterval = Duration.ofSeconds(10)
    }
}

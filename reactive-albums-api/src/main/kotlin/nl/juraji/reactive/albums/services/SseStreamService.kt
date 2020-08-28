package nl.juraji.reactive.albums.services

import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*

@Service
class SseStreamService {

    fun <T> asEventStream(source: Flux<T>): Flux<ServerSentEvent<T>> {
        val sourceStream: Flux<ServerSentEvent<T>> = sourceEventStream(source)
        val heartbeatStream: Flux<ServerSentEvent<T>> = buildHeartbeatEventStream()

        return Flux.merge(sourceStream, heartbeatStream)
    }

    private fun <T> sourceEventStream(source: Flux<T>): Flux<ServerSentEvent<T>> {
        return source.map {
            ServerSentEvent
                    .builder(it)
                    .id(UUID.randomUUID().toString())
                    .build()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> buildHeartbeatEventStream(): Flux<ServerSentEvent<T>> =
            Flux.interval(heartbeatInterval).map {
                ServerSentEvent
                        .builder<T>(HeartbeatMessage() as T)
                        .event("ping")
                        .build()
            }

    companion object {
        private val heartbeatInterval = Duration.ofSeconds(10)
    }

    internal data class HeartbeatMessage(
            val timestamp: Long = System.currentTimeMillis()
    )
}

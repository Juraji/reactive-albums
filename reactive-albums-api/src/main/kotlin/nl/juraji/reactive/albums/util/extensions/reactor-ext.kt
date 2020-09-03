package nl.juraji.reactive.albums.util.extensions

import reactor.core.publisher.Flux
import java.time.Duration

/**
 * Buffers the stream for a set amount of time.
 * Then emits the buffer with only the last updates for each identity
 */
fun <T> Flux<T>.bufferLastIdentity(bufferingTimespan: Duration, identity: (T) -> Any): Flux<List<T>> {
    return this
            .buffer(bufferingTimespan)
            .map { updates ->
                updates
                        .groupBy(identity)
                        .map { (_, list) -> list.last() }
            }
}

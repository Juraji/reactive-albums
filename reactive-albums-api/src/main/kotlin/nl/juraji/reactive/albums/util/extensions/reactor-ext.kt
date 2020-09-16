package nl.juraji.reactive.albums.util.extensions

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.time.Duration
import java.util.*

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

fun Mono<Boolean>.filterIsTrue() = this.filter { it }

fun <T> deferTo(scheduler: Scheduler, supplier: () -> T?): Mono<T> =
        Mono.defer { Mono.justOrEmpty(supplier()) }.subscribeOn(scheduler)

fun <T> deferOptionalTo(scheduler: Scheduler, supplier: () -> Optional<T>): Mono<T> =
        Mono.defer { Mono.justOrEmpty(supplier()) }.subscribeOn(scheduler)

fun <T> deferIterableTo(scheduler: Scheduler, supplier: () -> Iterable<T>): Flux<T> =
        Flux.defer { Flux.fromIterable(supplier()) }.subscribeOn(scheduler)

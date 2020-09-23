package nl.juraji.reactive.albums.util

import io.mockk.MockKAdditionalAnswerScope
import io.mockk.MockKStubScope
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <T, B> MockKStubScope<Mono<T>, B>.returnsEmptyMono(): MockKAdditionalAnswerScope<Mono<T>, B> =
        this.returns(Mono.empty())

infix fun <T, B> MockKStubScope<Mono<T>, B>.returnsMonoOf(value: T): MockKAdditionalAnswerScope<Mono<T>, B> =
        this.returns(Mono.just(value))

infix fun <T, B> MockKAdditionalAnswerScope<Mono<T>, B>.andThenMonoOf(value: T): MockKAdditionalAnswerScope<Mono<T>, B> =
        this.andThen(Mono.just(value))

fun <T, B> MockKStubScope<Flux<T>, B>.returnsEmptyFlux(): MockKAdditionalAnswerScope<Flux<T>, B> =
        this.returns(Flux.empty())

infix fun <T, B> MockKStubScope<Flux<T>, B>.returnsFluxOf(iterable: Iterable<T>): MockKAdditionalAnswerScope<Flux<T>, B> =
        this.returns(Flux.fromIterable(iterable))

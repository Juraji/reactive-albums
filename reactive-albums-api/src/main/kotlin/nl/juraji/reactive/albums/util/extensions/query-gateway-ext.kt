package nl.juraji.reactive.albums.util.extensions

import org.axonframework.messaging.responsetypes.ResponseType
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.queryhandling.SubscriptionQueryResult
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.reflect.KClass

fun <Q, R, C : ResponseType<R>> QueryGateway.subscriptionFlux(query: Q, responseType: C, useInitial: Boolean = true): Flux<R> {
    val sub: SubscriptionQueryResult<R, R> = subscriptionQuery(query, responseType, responseType)
    return if (useInitial) Flux.concat(sub.initialResult(), sub.updates()) else sub.updates()
}

fun <Q, R, C : KClass<R>> QueryGateway.subscriptionFlux(query: Q, responseType: C, useInitial: Boolean = true): Flux<R> {
    val sub: SubscriptionQueryResult<R, R> = subscriptionQuery(query, responseType.java, responseType.java)
    return if (useInitial) Flux.concat(sub.initialResult(), sub.updates()) else sub.updates()
}

fun <Q, R, C : KClass<R>> QueryGateway.subscribeToNextUpdate(query: Q, responseType: C): Mono<R> {
    val sub: SubscriptionQueryResult<R, R> = subscriptionQuery(query, responseType.java, responseType.java)
    return sub.updates().toMono()
}

fun <Q, R, C : KClass<R>> QueryGateway.subscribeToLastUpdate(query: Q, responseType: C): Mono<R> {
    val sub: SubscriptionQueryResult<R, R> = subscriptionQuery(query, responseType.java, responseType.java)
    return sub.updates().last()
}

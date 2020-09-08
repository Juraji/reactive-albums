package nl.juraji.reactive.albums.api

import org.axonframework.commandhandling.gateway.CommandGateway
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

abstract class CommandSenderService(
        private val commandGateway: CommandGateway,
) {

    fun <T> send(cmd: Any): Mono<T> =
            commandGateway.send<T>(cmd).toMono()

    fun <T> send(cmd: Any, defaultValue: T): Mono<T> =
            commandGateway.send<T>(cmd).toMono()
                    .switchIfEmpty(Mono.just(defaultValue))

    fun <T> sendAndCatch(cmd: Any): Mono<Result<T>> =
            commandGateway.send<T>(cmd).toMono()
                    .onErrorContinue { t, _ -> Result.failure<T>(t) }
                    .map { Result.success(it) }
}

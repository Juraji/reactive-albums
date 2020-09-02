package nl.juraji.reactive.albums.domain

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

object Validate {
    fun isTrue(assertion: Boolean, message: () -> String): Boolean =
            if (!assertion) throw ValidationException(message())
            else true

    fun isFalse(assertion: Boolean, message: () -> String): Boolean =
            if (assertion) throw ValidationException(message())
            else true

    fun isNotNull(value: Any?, message: () -> String): Boolean =
            if (value == null) throw ValidationException(message())
            else true
}

object ValidateAsync {
    private fun success(): Mono<Boolean> = Mono.just(true)
    private fun fail(message: () -> String): Mono<Boolean> = Mono.error { ValidationException(message()) }

    fun isTrue(assertion: Mono<Boolean>, message: () -> String): Mono<Boolean> =
            assertion.flatMap { isTrue ->
                if (isTrue) success()
                else fail(message)
            }

    fun isFalse(assertion: Mono<Boolean>, message: () -> String): Mono<Boolean> =
            assertion.flatMap { isTrue ->
                if (!isTrue) success()
                else fail(message)
            }

    fun <T : Any> isNotNull(value: Mono<T>, message: () -> String): Mono<Boolean> =
            value
                    .flatMap { success() }
                    .switchIfEmpty(fail(message))

    fun all(vararg validations: Mono<Boolean>): Mono<Boolean> =
            Flux.fromArray(validations)
                    .flatMap { x -> x }
                    .all { x -> x }
                    .flatMap { success() }

}

data class ValidationException(
        override val message: String,
) : IllegalArgumentException(message)

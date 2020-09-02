package nl.juraji.reactive.albums.domain

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class ValidateAsyncTest {

    @Test
    fun `isTrue should proceed when assertion is true`() {
        val mono = Mono.just(true)
        val validated = ValidateAsync.isTrue(mono) { "Should not throw" }

        StepVerifier
                .create(validated)
                .expectNext(true)
                .expectComplete()
                .verify()
    }

    @Test
    fun `isTrue should fail when assertion is false`() {
        val mono = Mono.just(false)
        val validated = ValidateAsync.isTrue(mono) { "Should throw" }

        StepVerifier
                .create(validated)
                .expectErrorMatches { it is ValidationException && it.message == "Should throw" }
                .verify()
    }

    @Test
    fun `isFalse should proceed when assertion is false`() {
        val mono = Mono.just(false)
        val validated = ValidateAsync.isFalse(mono) { "Should not throw" }

        StepVerifier
                .create(validated)
                .expectNext(true)
                .expectComplete()
                .verify()
    }

    @Test
    fun `isFalse should fail when assertion is true`() {
        val mono = Mono.just(true)
        val validated = ValidateAsync.isFalse(mono) { "Should throw" }

        StepVerifier
                .create(validated)
                .expectErrorMatches { it is ValidationException && it.message == "Should throw" }
                .verify()
    }

    @Test
    fun `isNotNull should proceed when mono is not empty`() {
        val mono = Mono.just("Something")
        val validated = ValidateAsync.isNotNull(mono) { "Should not throw" }

        StepVerifier
                .create(validated)
                .expectNext(true)
                .expectComplete()
                .verify()
    }

    @Test
    fun `isNotNull should fail when mono is empty`() {
        val mono = Mono.empty<String>()
        val validated = ValidateAsync.isNotNull(mono) { "Should throw" }

        StepVerifier
                .create(validated)
                .expectErrorMatches { it is ValidationException && it.message == "Should throw" }
                .verify()
    }

    @Test
    fun `all should proceed if all assertions complete`() {
        val validated = ValidateAsync.all(
                ValidateAsync.isTrue(Mono.just(true)) { "Should not throw" },
                ValidateAsync.isFalse(Mono.just(false)) { "Should not throw" }
        )

        StepVerifier
                .create(validated)
                .expectNext(true)
                .expectComplete()
                .verify()
    }

    @Test
    fun `all should fail if on or more assertions fail`() {
        val validated = ValidateAsync.all(
                ValidateAsync.isFalse(Mono.just(false)) { "Should not throw" },
                ValidateAsync.isTrue(Mono.just(false)) { "Should throw" }
        )

        StepVerifier
                .create(validated)
                .expectErrorMatches { it is ValidationException && it.message == "Should throw" }
                .verify()
    }
}

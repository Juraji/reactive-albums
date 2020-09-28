package nl.juraji.reactive.albums.util.extensions

import org.junit.jupiter.api.Test
import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import reactor.test.scheduler.VirtualTimeScheduler
import java.time.Duration

internal class SseExtKtTest {

    @Test
    fun toServerSentEvents() {
        val scheduler: VirtualTimeScheduler = VirtualTimeScheduler.getOrSet()

        val source: Flux<Long> = Flux.interval(Duration.ofSeconds(3), scheduler).take(3)
        val events: Flux<ServerSentEvent<Long?>> = source.toServerSentEvents(
                heartbeatDelay = Duration.ZERO,
                heartbeatInterval = Duration.ofSeconds(6)
        )

        StepVerifier.withVirtualTime { events }
                .expectNextMatches { it.event() == "ping" }
                .thenAwait(Duration.ofSeconds(3))
                .expectNextMatches { it.data() == 0L }
                .thenAwait(Duration.ofSeconds(3))
                .expectNextMatches { it.event() == "ping" }
                .expectNextMatches { it.data() == 1L }
                .thenAwait(Duration.ofSeconds(3))
                .expectNextMatches { it.data() == 2L }
                .thenAwait(Duration.ofSeconds(3))
                .expectNextMatches { it.event() == "ping" }
                .thenAwait(Duration.ofSeconds(6))
                .expectNextMatches { it.event() == "ping" }
                .thenCancel()
                .verify()
    }
}

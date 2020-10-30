package nl.juraji.reactive.albums.api.pictures

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.EventType
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveEvent
import nl.juraji.reactive.albums.util.andThenMonoOf
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import nl.juraji.reactive.albums.util.returnsFluxOf
import nl.juraji.reactive.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.scheduler.VirtualTimeScheduler
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class PictureEventsServiceTest {

    private val fixture = Fixture {
        register(PictureType::class) { PictureType.JPEG }
        register(EventType::class) { EventType.UPSERT }
        register(LocalDateTime::class) { LocalDateTime.now() }
    }

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @MockK
    private lateinit var duplicateMatchRepository: DuplicateMatchRepository

    @InjectMockKs
    private lateinit var pictureEventsService: PictureEventsService

    @Test
    fun `getDuplicateMatchCountStream should result in a stream of the current count, initial and on update`() {
        VirtualTimeScheduler.getOrSet()

        every { duplicateMatchRepository.count() } returnsMonoOf 10L andThenMonoOf 21L andThenMonoOf 36L
        every { duplicateMatchRepository.subscribeToAll() } returnsFluxOf listOf(fixture.next(), fixture.next())

        val result = pictureEventsService.getDuplicateMatchCountStream()

        StepVerifier.create(result)
                .expectNextMatches { it.event() == "ping" }
                .expectNextMatches { it.data() == 10L }
                .expectNextMatches { it.data() == 21L }
                .expectNextMatches { it.data() == 36L }
                .thenCancel()
                .verify()
    }
}

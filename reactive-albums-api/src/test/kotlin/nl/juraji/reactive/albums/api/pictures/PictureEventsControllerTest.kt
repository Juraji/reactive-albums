package nl.juraji.reactive.albums.api.pictures

import com.fasterxml.jackson.databind.ObjectMapper
import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.reactive.albums.api.ApiTestConfiguration
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.EventType
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveEvent
import nl.juraji.reactive.albums.util.returnsFluxOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.FluxExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.returnResult
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@Import(ApiTestConfiguration::class)
@WebFluxTest(PictureEventsController::class)
@AutoConfigureWebTestClient
internal class PictureEventsControllerTest {

    @MockkBean
    private lateinit var pictureEventsService: PictureEventsService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    @Qualifier("objectMapper")
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `getDuplicateMatchCount should render duplicate match count stream`() {
        val fixture = Fixture {
            register(ServerSentEvent::class) { ServerSentEvent.builder<Long>(nextLong()).build() }
        }

        val streamData: List<ServerSentEvent<Long?>> = listOf(
                fixture.next(),
                fixture.next(),
                fixture.next(),
        )

        every { pictureEventsService.getDuplicateMatchCountStream() } returnsFluxOf streamData

        val returnResult: FluxExchangeResult<ServerSentEvent<Long?>> = webTestClient.get()
                .uri("/api/events/duplicate-match-count")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk
                .returnResult()

        StepVerifier.create(returnResult.responseBody.map { it.data()!! })
                .expectNextSequence(streamData.map { it.data() })
                .thenCancel()
                .verify()
    }

    @Test
    fun getAllDuplicateMatches() {
        val fixture = Fixture {
            register(DuplicateMatchProjection::class) { DuplicateMatchProjection(nextString(), nextString(), nextString(), nextInt()) }
            register(ServerSentEvent::class) { ServerSentEvent.builder<ReactiveEvent<DuplicateMatchProjection>>(next()).build() }
            register(EventType::class) { EventType.UPSERT }
        }
        val streamData: List<ServerSentEvent<ReactiveEvent<DuplicateMatchProjection>?>> = listOf(
                fixture.next(),
                fixture.next(),
                fixture.next(),
        )

        every { pictureEventsService.getAllDuplicateMatchesStream() } returnsFluxOf streamData

        val returnResult: FluxExchangeResult<ServerSentEvent<ReactiveEvent<DuplicateMatchProjection>?>> = webTestClient.get()
                .uri("/api/events/duplicate-matches")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk
                .returnResult()

        StepVerifier.create(returnResult.responseBody.map { it.data()!! })
                .expectNextSequence(streamData.map { it.data() })
                .thenCancel()
                .verify()
    }

    @Test
    fun getPictureDuplicateMatches() {
        val fixture = Fixture {
            register(DuplicateMatchProjection::class) { DuplicateMatchProjection(nextString(), nextString(), nextString(), nextInt()) }
            register(ServerSentEvent::class) { ServerSentEvent.builder<ReactiveEvent<DuplicateMatchProjection>>(next()).build() }
            register(EventType::class) { EventType.UPSERT }
        }

        val pictureId = PictureId()
        val streamData: List<ServerSentEvent<ReactiveEvent<DuplicateMatchProjection>?>> = listOf(
                fixture.next(),
                fixture.next(),
                fixture.next(),
        )

        every { pictureEventsService.getDuplicateMatchStreamByPictureId(pictureId.identifier) } returnsFluxOf streamData

        val returnResult: FluxExchangeResult<ServerSentEvent<ReactiveEvent<DuplicateMatchProjection>?>>  = webTestClient.get()
                .uri("/api/events/duplicate-matches/$pictureId")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk
                .returnResult()

        StepVerifier.create(returnResult.responseBody.map { it.data()!! })
                .expectNextSequence(streamData.map { it.data() })
                .thenCancel()
                .verify()
    }
}

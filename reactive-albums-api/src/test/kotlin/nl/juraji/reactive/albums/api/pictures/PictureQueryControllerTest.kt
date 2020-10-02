package nl.juraji.reactive.albums.api.pictures

import com.fasterxml.jackson.databind.ObjectMapper
import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import nl.juraji.reactive.albums.api.ApiTestConfiguration
import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.util.returnsMonoOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@Import(ApiTestConfiguration::class)
@WebFluxTest(PictureQueryController::class)
@AutoConfigureWebTestClient
internal class PictureQueryControllerTest {

    private val fixture = Fixture {
        register(PictureType::class) { PictureType.JPEG }
        register(LocalDateTime::class) { LocalDateTime.now() }
        register(PictureProjection::class) { PictureProjection(nextString(), nextString(), nextString(), nextString(), next()) }
    }

    @MockkBean
    private lateinit var pictureQueryService: PictureQueryService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    @Qualifier("objectMapper")
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `getPictures should render pictures page with default opts`() {
        val pictures: List<PictureProjection> = listOf(fixture.next(), fixture.next(), fixture.next())

        val pageableSlot: CapturingSlot<Pageable> = slot()
        every { pictureQueryService.getPictures(null, capture(pageableSlot)) } answers {
            Mono.just(PageImpl(pictures, secondArg(), 6))
        }

        val exchangeResult: EntityExchangeResult<String> = webTestClient.get()
                .uri("/api/pictures")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().returnResult()

        val expectedJson: String = objectMapper.writeValueAsString(PageImpl(pictures, pageableSlot.captured, 6));
        assertEquals(expectedJson, exchangeResult.responseBody)
        assertEquals(0, pageableSlot.captured.pageNumber)
        assertEquals(50, pageableSlot.captured.pageSize)
        assertEquals(Sort.by(Sort.Direction.DESC, "displayName"), pageableSlot.captured.sort)
    }

    @Test
    fun `getPictures should render pictures page with custom opts`() {
        val pictures: List<PictureProjection> = listOf(fixture.next(), fixture.next(), fixture.next())

        val filterSlot: CapturingSlot<String> = slot()
        val pageableSlot: CapturingSlot<Pageable> = slot()
        every { pictureQueryService.getPictures(capture(filterSlot), capture(pageableSlot)) } answers {
            Mono.just(PageImpl(pictures, secondArg(), 6))
        }

        val exchangeResult: EntityExchangeResult<String> = webTestClient.get()
                .uri("/api/pictures?page=1&size=10&sort=location,asc&filter=tag:my+tag")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().returnResult()

        val expectedJson: String = objectMapper.writeValueAsString(PageImpl(pictures, pageableSlot.captured, 6));
        assertEquals(expectedJson, exchangeResult.responseBody)
        assertEquals(1, pageableSlot.captured.pageNumber)
        assertEquals(10, pageableSlot.captured.pageSize)
        assertEquals(Sort.by(Sort.Direction.ASC, "location"), pageableSlot.captured.sort)
        assertEquals("tag:my tag", filterSlot.captured)
    }

    @Test
    fun `getPicture should render expected picture`() {
        val picture: PictureProjection = fixture.next()

        every { pictureQueryService.getPicture(picture.id) } returnsMonoOf picture

        webTestClient.get()
                .uri("/api/pictures/${picture.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<PictureProjection>().isEqualTo(picture)
    }
}

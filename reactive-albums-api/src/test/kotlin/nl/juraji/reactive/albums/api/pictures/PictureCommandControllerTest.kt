package nl.juraji.reactive.albums.api.pictures

import com.fasterxml.jackson.databind.ObjectMapper
import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import nl.juraji.reactive.albums.api.ApiTestConfiguration
import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.TagLink
import nl.juraji.reactive.albums.util.returnsEmptyMono
import nl.juraji.reactive.albums.util.returnsFluxOf
import nl.juraji.reactive.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@Import(ApiTestConfiguration::class)
@WebFluxTest(PictureCommandController::class)
@AutoConfigureWebTestClient
internal class PictureCommandControllerTest {

    private val fixture = Fixture {
        register(PictureType::class) { PictureType.JPEG }
        register(LocalDateTime::class) { LocalDateTime.now() }
    }

    @MockkBean
    private lateinit var pictureCommandsService: PictureCommandsService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    @Qualifier("objectMapper")
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `rescanDuplicates should init duplicate scan`() {
        val pictureId = PictureId()

        every { pictureCommandsService.rescanDuplicates(pictureId.identifier) } returnsMonoOf pictureId

        webTestClient.post()
                .uri("/api/pictures/$pictureId/rescan-duplicates")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk
                .expectBody<String>()
                .isEqualTo(pictureId.identifier)
    }

    @Test
    fun `unlinkDuplicateMatch should init match unlink`() {
        val pictureId = PictureId()
        val matchId = DuplicateMatchId()

        every { pictureCommandsService.unlinkDuplicateMatch(pictureId.identifier, matchId.identifier) }.returnsEmptyMono()

        webTestClient.delete()
                .uri("/api/pictures/$pictureId/duplicate-matches/$matchId")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody().isEmpty
    }

    @Test
    fun `movePicture should init picture move`() {
        val pictureId = PictureId()
        val targetLocation = "location"

        val resultProjection: PictureProjection = fixture.next()
        val expectedJson: String = objectMapper.writeValueAsString(resultProjection)

        every { pictureCommandsService.movePicture(pictureId.identifier, targetLocation) } returnsMonoOf resultProjection

        webTestClient.post()
                .uri("/api/pictures/$pictureId/move?targetLocation=$targetLocation")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody().json(expectedJson)
    }

    @Test
    fun `deletePicture should init picture delete`() {
        val pictureId = PictureId()

        every { pictureCommandsService.deletePicture(pictureId.identifier, true) } returnsMonoOf pictureId

        webTestClient.delete()
                .uri("/api/pictures/$pictureId?deletePhysicalFile=true")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk
                .expectBody<String>()
                .isEqualTo(pictureId.identifier)
    }

    @Test
    fun `linkTag should init tag link`() {
        val pictureId = PictureId()
        val tagId = TagId()

        val resultProjection: List<TagLink> = listOf(mockk(relaxed = true))

        every { pictureCommandsService.linkTag(pictureId.identifier, tagId.identifier) } returnsFluxOf resultProjection

        webTestClient.post()
                .uri("/api/pictures/$pictureId/tags/$tagId")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList<TagLink>().hasSize(1)
    }

    @Test
    fun `unlinkTag should init tag unlink`() {
        val pictureId = PictureId()
        val tagId = TagId()

        val resultProjection: List<TagLink> = listOf(mockk(relaxed = true))

        every { pictureCommandsService.unlinkTag(pictureId.identifier, tagId.identifier) } returnsFluxOf resultProjection

        webTestClient.delete()
                .uri("/api/pictures/$pictureId/tags/$tagId")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList<TagLink>().hasSize(1)
    }
}

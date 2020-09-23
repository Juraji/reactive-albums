package nl.juraji.reactive.albums.api.pictures

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.reactive.albums.api.ApiTestConfiguration
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.query.thumbnails.Thumbnail
import nl.juraji.reactive.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@Import(ApiTestConfiguration::class)
@WebFluxTest(PictureImageController::class)
@AutoConfigureWebTestClient
internal class PictureImageControllerTest {

    @MockkBean
    private lateinit var pictureImageService: PictureImageService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `getPictureThumbnailImage should render thumbnail resource`() {
        val pictureId = PictureId()
        val pictureBytes: ByteArray = ByteArray(1).let {
            it[0] = Byte.MAX_VALUE
            it
        }

        val mediaType = MediaType.IMAGE_JPEG
        val thumbnail = Thumbnail(id = "", thumbnail = pictureBytes, lastModifiedTime = LocalDateTime.now(), contentType = mediaType.toString())

        every { pictureImageService.getThumbnail(pictureId.identifier) } returnsMonoOf thumbnail

        webTestClient.get()
                .uri("/api/pictures/$pictureId/thumbnail")
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.IMAGE_JPEG)
                .expectBody<ByteArray>().isEqualTo(pictureBytes)
    }

    @Test
    fun `getPictureImage should render picture image resource`() {
        val pictureId = PictureId()

        every { pictureImageService.getPictureLocation(pictureId.identifier) } returnsMonoOf "./src/test/resources/test.jpg"

        webTestClient.get()
                .uri("/api/pictures/$pictureId/image")
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.IMAGE_JPEG)
                .expectHeader().contentLength(1553)
    }
}

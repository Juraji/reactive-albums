package nl.juraji.reactive.albums.api.directories

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.reactive.albums.api.ApiTestConfiguration
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.util.returnsFluxOf
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
@WebFluxTest(DirectoriesQueryController::class)
@AutoConfigureWebTestClient
internal class DirectoriesQueryControllerTest {

    private val fixture = Fixture {
        register(LocalDateTime::class) { LocalDateTime.now() }
    }

    @MockkBean
    private lateinit var directoriesQueryService: DirectoriesQueryService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `getAllDirectories should render a list of directories`() {
        val directories: List<DirectoryProjection> = listOf(fixture.next(), fixture.next())

        every { directoriesQueryService.getAllDirectories() } returnsFluxOf directories

        webTestClient.get()
                .uri("/api/directories")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<List<DirectoryProjection>>().isEqualTo(directories)
    }
}

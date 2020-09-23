package nl.juraji.reactive.albums.api.directories

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import nl.juraji.reactive.albums.api.ApiTestConfiguration
import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.util.returnsFluxOf
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
import org.springframework.test.web.reactive.server.FluxExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.test.StepVerifier
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@Import(ApiTestConfiguration::class)
@WebFluxTest(DirectoryCommandController::class)
@AutoConfigureWebTestClient
internal class DirectoryCommandControllerTest {

    private val fixture = Fixture {
        register(LocalDateTime::class) { LocalDateTime.now() }
        register(Path::class) { Paths.get("./") }
    }

    @MockkBean
    private lateinit var directoryCommandService: DirectoryCommandService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `registerDirectory should init register directory`() {
        val dtoBody: RegisterDirectoryDto = fixture.next()
        val resultDirectories: List<DirectoryProjection> = listOf(
                fixture.next<DirectoryProjection>().copy(location = dtoBody.location.toString()),
                fixture.next<DirectoryProjection>().copy(location = dtoBody.location.resolve("child1").toString()),
                fixture.next<DirectoryProjection>().copy(location = dtoBody.location.resolve("child2").toString()),
        )

        every { directoryCommandService.registerDirectory(any(), dtoBody.recursive) } returnsFluxOf resultDirectories

        val exchangeResult: FluxExchangeResult<DirectoryProjection> = webTestClient.post()
                .uri("/api/directories")
                .bodyValue(dtoBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult()

        StepVerifier.create(exchangeResult.responseBody)
                .expectNextSequence(resultDirectories)
                .verifyComplete()
    }

    @Test
    fun `updateDirectory should init update directory`() {
        val dtoBody: UpdateDirectoryDto = fixture.next()
        val directory: DirectoryProjection = fixture.next<DirectoryProjection>().copy(automaticScanEnabled = dtoBody.automaticScanEnabled!!)

        every { directoryCommandService.updateDirectory(DirectoryId(directory.id), dtoBody.automaticScanEnabled) } returnsMonoOf directory

        val exchangeResult: FluxExchangeResult<DirectoryProjection> = webTestClient.put()
                .uri("/api/directories/${directory.id}")
                .bodyValue(dtoBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult()

        StepVerifier.create(exchangeResult.responseBody)
                .expectNext(directory)
                .verifyComplete()
    }

    @Test
    fun `unregisterDirectory should init unregister directory`() {
        val directoryId = DirectoryId()
        val directoryIds: List<DirectoryId> = listOf(directoryId, fixture.next(), fixture.next())

        every { directoryCommandService.unregisterDirectory(directoryId, true) } returnsFluxOf directoryIds

        val exchangeResult: FluxExchangeResult<DirectoryId> = webTestClient.delete()
                .uri("/api/directories/$directoryId?recursive=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .returnResult()

        StepVerifier.create(exchangeResult.responseBody)
                .expectNextSequence(directoryIds)
                .verifyComplete()
    }
}

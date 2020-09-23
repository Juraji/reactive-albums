package nl.juraji.reactive.albums.api.directories

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import nl.juraji.reactive.albums.util.returnsFluxOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class DirectoriesQueryServiceTest {

    private val fixture = Fixture {
        register(LocalDateTime::class) { LocalDateTime.now() }
    }

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @InjectMockKs
    private lateinit var directoriesQueryService: DirectoriesQueryService

    @Test
    fun `getAllDirectories should result in a flux of all directories`() {
        val directories: List<DirectoryProjection> = listOf(fixture.next(), fixture.next())

        every { directoryRepository.findAll() } returnsFluxOf directories

        val result: Flux<DirectoryProjection> = directoriesQueryService.getAllDirectories()

        StepVerifier.create(result)
                .expectNextSequence(directories)
                .verifyComplete()
    }
}

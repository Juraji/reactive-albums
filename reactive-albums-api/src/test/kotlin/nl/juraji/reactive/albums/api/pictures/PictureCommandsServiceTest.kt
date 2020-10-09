package nl.juraji.reactive.albums.api.pictures

import com.marcellogalhardo.fixture.Fixture
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.*
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.util.returnsMonoOf
import nl.juraji.reactive.albums.util.toCompletableFuture
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.nio.file.Paths

@ExtendWith(MockKExtension::class)
internal class PictureCommandsServiceTest {

    @MockK
    private lateinit var commandGateway: CommandGateway

    @MockK
    private lateinit var duplicateMatchRepository: DuplicateMatchRepository

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @InjectMockKs
    private lateinit var pictureCommandsService: PictureCommandsService

    private val fixture = Fixture {}
    private val pictureId = PictureId()

    @BeforeEach
    internal fun setUp() {
        every { commandGateway.send<PictureId>(any()) } returns pictureId.toCompletableFuture()
    }

    @Test
    fun `rescanDuplicates should result in ScanDuplicatesCommand`() {
        val result: Mono<PictureId> = pictureCommandsService.rescanDuplicates(pictureId.identifier)

        StepVerifier.create(result)
                .expectNext(pictureId)
                .expectComplete()
                .verify()

        verify { commandGateway.send<Any>(ScanDuplicatesCommand(pictureId)) }
    }

    @Test
    fun `movePicture should result in MovePictureCommand`() {
        val targetDirectory = DirectoryProjection(
                id = fixture.nextString(),
                location = fixture.nextString(),
                displayName = fixture.nextString(),
                automaticScanEnabled = fixture.nextBoolean()
        )

        val resultProjection: PictureProjection = mockk(relaxed = true)
        every { directoryRepository.findById(targetDirectory.id) } returnsMonoOf targetDirectory
        every { pictureRepository.subscribeFirst(any(), any()) } returnsMonoOf resultProjection

        val result: Mono<PictureProjection> = pictureCommandsService.movePicture(pictureId.identifier, targetDirectory.id)

        StepVerifier.create(result)
                .expectNext(resultProjection)
                .expectComplete()
                .verify()

        verify(exactly = 1) {
            commandGateway.send<Any>(MovePictureCommand(
                    pictureId,
                    DirectoryId(targetDirectory.id),
                    Paths.get(targetDirectory.location)
            ))
        }
        confirmVerified(commandGateway)
    }

    @Test
    fun `deletePicture should result in DeletePictureCommand`() {
        val result: Mono<PictureId> = pictureCommandsService.deletePicture(pictureId.identifier, true)

        StepVerifier.create(result)
                .expectNext(pictureId)
                .expectComplete()
                .verify()

        verify(exactly = 1) { commandGateway.send<Any>(DeletePictureCommand(pictureId, true)) }
        confirmVerified(commandGateway)
    }

    @Test
    fun `linkTag should result in LinkTagCommand`() {
        val tagId = TagId()

        val pictureProjection: PictureProjection = mockk {
            every { tags } returns setOf(mockk(), mockk())
        }

        every { pictureRepository.subscribeFirst(any(), any()) } returnsMonoOf pictureProjection

        val result: Flux<TagProjection> = pictureCommandsService.linkTag(pictureId.identifier, tagId.identifier)

        StepVerifier.create(result)
                .expectNextSequence(pictureProjection.tags)
                .expectComplete()
                .verify()

        verify(exactly = 1) { commandGateway.send<Any>(LinkTagCommand(pictureId, tagId)) }
        confirmVerified(commandGateway)
    }

    @Test
    fun `unlinkTag should result in UnlinkTagCommand`() {
        val tagId = TagId()

        val pictureProjection: PictureProjection = mockk {
            every { tags } returns setOf(mockk(), mockk())
        }

        every { pictureRepository.subscribeFirst(any(), any()) } returnsMonoOf pictureProjection

        val result: Flux<TagProjection> = pictureCommandsService.unlinkTag(pictureId.identifier, tagId.identifier)

        StepVerifier.create(result)
                .expectNextSequence(pictureProjection.tags)
                .expectComplete()
                .verify()

        verify(exactly = 1) { commandGateway.send<Any>(UnlinkTagCommand(pictureId, tagId)) }
        confirmVerified(commandGateway)
    }
}

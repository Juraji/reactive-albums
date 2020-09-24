package nl.juraji.reactive.albums.api.directories

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.reactive.albums.domain.ValidationException
import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.directories.commands.DirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.RegisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UnregisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UpdateDirectoryCommand
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.util.returnsFluxOf
import nl.juraji.reactive.albums.util.returnsManyMonoOf
import nl.juraji.reactive.albums.util.returnsMonoOf
import nl.juraji.reactive.albums.util.toCompletableFuture
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class DirectoryCommandServiceTest {

    private val fixture = Fixture {
        register(Path::class) { Paths.get(nextString()) }
        register(LocalDateTime::class) { LocalDateTime.now() }
    }

    @MockK
    private lateinit var commandGateway: CommandGateway

    @MockK
    private lateinit var directoryRepository: DirectoryRepository

    @MockK
    private lateinit var fileSystemService: FileSystemService

    @InjectMockKs
    private lateinit var directoryCommandService: DirectoryCommandService

    @Test
    fun `registerDirectory should fail when directory already is registered`() {
        val location: Path = fixture.next()
        val recursive: Boolean = fixture.nextBoolean()

        every { directoryRepository.existsByLocation(location.toString()) } returnsMonoOf true
        every { fileSystemService.exists(location) } returnsMonoOf true

        val result: Flux<DirectoryProjection> = directoryCommandService.registerDirectory(location, recursive)

        StepVerifier.create(result)
                .expectError(ValidationException::class.java)
                .verify()
    }

    @Test
    fun `registerDirectory should fail when directory physically not exists`() {
        val location: Path = fixture.next()
        val recursive: Boolean = fixture.nextBoolean()

        every { directoryRepository.existsByLocation(location.toString()) } returnsMonoOf false
        every { fileSystemService.exists(location) } returnsMonoOf false

        val result: Flux<DirectoryProjection> = directoryCommandService.registerDirectory(location, recursive)

        StepVerifier.create(result)
                .expectError(ValidationException::class.java)
                .verify()
    }

    @Test
    fun `registerDirectory should result in single RegisterDirectoryCommand when not recursive`() {
        val location: Path = fixture.next()
        val recursive = false
        val directory: DirectoryProjection = fixture.next()

        every { directoryRepository.existsByLocation(location.toString()) } returnsMonoOf false
        every { fileSystemService.exists(location) } returnsMonoOf true
        every { directoryRepository.subscribeFirst(any(), any()) } returnsMonoOf directory


        val commandSlot: CapturingSlot<RegisterDirectoryCommand> = slot()
        every { commandGateway.send<DirectoryId>(capture(commandSlot)) } answers {
            firstArg<DirectoryCommand>().directoryId.toCompletableFuture()
        }

        val result: Flux<DirectoryProjection> = directoryCommandService.registerDirectory(location, recursive)

        StepVerifier.create(result)
                .expectNext(directory)
                .verifyComplete()

        assertEquals(location, commandSlot.captured.location)
    }

    @Test
    fun `registerDirectory should result in one or more RegisterDirectoryCommand when recursive`() {
        val location: Path = fixture.next()
        val recursive = true
        val childDirectories: List<DirectoryProjection> = listOf(
                fixture.next<DirectoryProjection>().copy(location = location.toString()),
                fixture.next(),
                fixture.next()
        )

        every { directoryRepository.existsByLocation(any()) } returnsManyMonoOf listOf(false, false, true, false)
        every { directoryRepository.subscribeFirst(any(), any()) } returnsManyMonoOf childDirectories
        every { fileSystemService.exists(any()) } returnsMonoOf true
        every { fileSystemService.listDirectoriesRecursive(location) } returnsFluxOf childDirectories.map { Paths.get(it.location) }

        val commandSlot: MutableList<RegisterDirectoryCommand> = mutableListOf()
        every { commandGateway.send<DirectoryId>(capture(commandSlot)) } answers {
            firstArg<DirectoryCommand>().directoryId.toCompletableFuture()
        }

        val result: Flux<DirectoryProjection> = directoryCommandService.registerDirectory(location, recursive)

        StepVerifier.create(result)
                .expectNextSequence(childDirectories.filterIndexed { i, _ -> i != 2 })
                .verifyComplete()

        assertEquals(2, commandSlot.size)
        assertEquals(location, commandSlot[0].location)
        assertEquals(childDirectories[2].location, commandSlot[1].location.toString())
    }

    @Test
    fun `unregisterDirectory should result in one UnregisterDirectoryCommand when not recursive`() {
        val directoryId = DirectoryId()
        val recursive = false

        every { commandGateway.send<DirectoryId>(any()) } answers { firstArg<DirectoryCommand>().directoryId.toCompletableFuture() }

        val result: Flux<DirectoryId> = directoryCommandService.unregisterDirectory(directoryId, recursive)

        StepVerifier.create(result)
                .expectNext(directoryId)
                .verifyComplete()

        verify { commandGateway.send<Any>(UnregisterDirectoryCommand(directoryId)) }
        confirmVerified(commandGateway)
    }

    @Test
    fun `unregisterDirectory should result in one UnregisterDirectoryCommand when recursive`() {
        val directories: List<DirectoryProjection> = listOf(fixture.next(), fixture.next(), fixture.next())
        val rootDirectory = directories[0]
        val rootDirectoryId = DirectoryId(directories[0].id)
        val subDirectoryId1 = DirectoryId(directories[1].id)
        val subDirectoryId2 = DirectoryId(directories[2].id)
        val recursive = true

        every { directoryRepository.findById(rootDirectory.id) } returnsMonoOf rootDirectory
        every { directoryRepository.findAllByLocationStartsWith(directories[0].location) } returnsFluxOf directories
        every { commandGateway.send<DirectoryId>(any()) } answers { firstArg<DirectoryCommand>().directoryId.toCompletableFuture() }

        val result: Flux<DirectoryId> = directoryCommandService.unregisterDirectory(rootDirectoryId, recursive)

        StepVerifier.create(result)
                .expectNextSequence(listOf(rootDirectoryId, subDirectoryId1, subDirectoryId2))
                .verifyComplete()

        verify { commandGateway.send<Any>(UnregisterDirectoryCommand(rootDirectoryId)) }
        verify { commandGateway.send<Any>(UnregisterDirectoryCommand(subDirectoryId1)) }
        verify { commandGateway.send<Any>(UnregisterDirectoryCommand(subDirectoryId2)) }
        confirmVerified(commandGateway)
    }

    @Test
    fun `updateDirectory should result in UpdateDirectoryCommand`() {
        val directory: DirectoryProjection = fixture.next()
        val directoryId = DirectoryId(directory.id)

        every { commandGateway.send<DirectoryId>(any()) } answers { firstArg<DirectoryCommand>().directoryId.toCompletableFuture() }
        every { directoryRepository.subscribeFirst(any(), any()) } returnsMonoOf directory

        val result: Mono<DirectoryProjection> = directoryCommandService.updateDirectory(directoryId, true)

        StepVerifier.create(result)
                .expectNext(directory)
                .verifyComplete()

        verify { commandGateway.send<Any>(UpdateDirectoryCommand(directoryId, true)) }
        confirmVerified(commandGateway)
    }
}

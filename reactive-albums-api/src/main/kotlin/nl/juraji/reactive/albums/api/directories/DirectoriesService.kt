package nl.juraji.reactive.albums.api.directories

import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.directories.commands.RegisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UnregisterDirectoryCommand
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.handlers.FindDirectoryById
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.util.extensions.subscribeToNextUpdate
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.streams.toList

@Service
class DirectoriesService(
        private val directoryRepository: DirectoryRepository,
        private val fileSystemService: FileSystemService,
        private val commandGateway: CommandGateway,
        private val queryGateway: QueryGateway,
) {

    fun registerDirectory(location: Path, recursive: Boolean): Flux<DirectoryProjection> {
        Validate.isFalse(directoryRepository.existsByLocation(location = location.toString())) { "Directory $location is already registered" }
        Validate.isTrue(fileSystemService.exists(location)) { "Directory $location does not exist" }

        val directories = if (recursive) {
            fileSystemService.listDirectoriesRecursive(location).toList()
                    .filter { !directoryRepository.existsByLocation(it.toString()) }
        } else {
            listOf(location)
        }

        val sources: List<Mono<DirectoryProjection>> = directories
                .map { RegisterDirectoryCommand(directoryId = DirectoryId(), location = it) }
                .map {
                    commandGateway.send<DirectoryId>(it)
                    it.directoryId
                }
                .map {
                    queryGateway.subscribeToNextUpdate(
                            FindDirectoryById(it),
                            DirectoryProjection::class
                    )
                }

        return Flux.merge(sources)
    }

    fun unregisterDirectory(directoryId: DirectoryId, recursive: Boolean): Mono<Unit> {
        val directoryIds = if (recursive) {
            val parent = directoryRepository.findById(directoryId.identifier).get()
            directoryRepository.findAllSubdirectoriesByLocation(parent.location).map { it.id }
        } else {
            listOf(directoryId)
        }

        val futures = directoryIds
                .map { UnregisterDirectoryCommand(directoryId = DirectoryId()) }
                .map { commandGateway.send<Unit>(it) }
                .toTypedArray()

        return Mono.create { sink ->
            CompletableFuture
                    .allOf(*futures)
                    .thenRun { sink.success() }
                    .exceptionally {
                        sink.error(it)
                        null
                    }
        }
    }
}

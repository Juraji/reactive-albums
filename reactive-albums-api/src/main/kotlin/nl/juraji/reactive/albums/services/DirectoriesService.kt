package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.api.CommandSenderService
import nl.juraji.reactive.albums.domain.ValidateAsync
import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.directories.commands.RegisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UnregisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UpdateDirectoryCommand
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.bool.not
import java.nio.file.Path
import java.time.Duration

@Service
class DirectoriesService(
        commandGateway: CommandGateway,
        private val directoryRepository: DirectoryRepository,
        private val fileSystemService: FileSystemService,
) : CommandSenderService(commandGateway) {

    fun registerDirectory(location: Path, recursive: Boolean): Flux<DirectoryProjection> {
        return ValidateAsync.all(
                ValidateAsync.isFalse(directoryRepository.existsByLocation(location = location.toString())) { "Directory $location is already registered" },
                ValidateAsync.isTrue(fileSystemService.exists(location).toMono()) { "Directory $location does not exist" }
        ).flatMapMany {
            val directories: Flux<Path> = if (recursive) {
                fileSystemService.listDirectoriesRecursive(location).toFlux()
                        .filterWhen { loc -> directoryRepository.existsByLocation(loc.toString()).not() }
            } else {
                Flux.fromIterable(listOf(location))
            }

            directories
                    .map { RegisterDirectoryCommand(directoryId = DirectoryId(), location = it) }
                    .flatMap { send<DirectoryId>(it) }
                    .flatMap { id -> directoryRepository.subscribeFirst(updateTimeout) { it.id == id.identifier } }
        }
    }

    fun unregisterDirectory(directoryId: DirectoryId, recursive: Boolean): Mono<List<String>> {
        val directoryIds: Flux<DirectoryId> = if (recursive) {
            directoryRepository
                    .findById(directoryId.identifier)
                    .flatMapMany { directoryRepository.findAllByLocationStartsWith(it.location) }
                    .map { DirectoryId(it.id) }
        } else {
            Flux.fromIterable(listOf(directoryId))
        }

        return directoryIds
                .map { UnregisterDirectoryCommand(directoryId = it) }
                .flatMap { send<DirectoryId>(it) }
                .map { it.identifier }
                .collectList()
    }

    fun updateDirectory(directoryId: DirectoryId, automaticScanEnabled: Boolean?): Mono<DirectoryProjection> {
        val command = UpdateDirectoryCommand(
                directoryId = directoryId,
                automaticScanEnabled = automaticScanEnabled
        )

        return send<DirectoryId>(command)
                .flatMap { id -> directoryRepository.subscribeFirst(updateTimeout) { it.id == id.identifier } }
    }

    companion object {
        val updateTimeout: Duration = Duration.ofSeconds(30)
    }
}

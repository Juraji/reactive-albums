package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.domain.ValidateAsync
import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.directories.commands.RegisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UnregisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UpdateDirectoryCommand
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import nl.juraji.reactive.albums.services.FileSystemService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.nio.file.Path

@Service
class DirectoriesService(
        private val directoryRepository: DirectoryRepository,
        private val fileSystemService: FileSystemService,
        private val commandGateway: CommandGateway,
) {
    fun registerDirectory(location: Path, recursive: Boolean): Flux<DirectoryProjection> {
        return ValidateAsync.all(
                ValidateAsync.isFalse(directoryRepository.existsByLocation(location = location.toString())) { "Directory $location is already registered" },
                ValidateAsync.isTrue(fileSystemService.exists(location).toMono()) { "Directory $location does not exist" }
        ).flatMapMany {
            val directories = if (recursive) {
                fileSystemService.listDirectoriesRecursive(location).toFlux()
                        .filterWhen { loc -> directoryRepository.existsByLocation(loc.toString()).map { exists -> !exists } }
            } else {
                Flux.fromIterable(listOf(location))
            }

            directories
                    .map { RegisterDirectoryCommand(directoryId = DirectoryId(), location = it) }
                    .flatMap { commandGateway.send<DirectoryId>(it).toMono() }
                    .flatMap { id -> directoryRepository.subscribeFirst { it.id == id.identifier } }
        }
    }

    fun unregisterDirectory(directoryId: DirectoryId, recursive: Boolean): Flux<DirectoryId> {
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
                .flatMap { commandGateway.send<DirectoryId>(it).toMono() }
    }

    fun updateDirectory(directoryId: DirectoryId, automaticScanEnabled: Boolean?): Mono<DirectoryProjection> {
        val command = UpdateDirectoryCommand(
                directoryId = directoryId,
                automaticScanEnabled = automaticScanEnabled
        )

        return commandGateway.send<DirectoryId>(command).toMono()
                .flatMap { id -> directoryRepository.subscribeFirst { it.id == id.identifier } }
    }
}

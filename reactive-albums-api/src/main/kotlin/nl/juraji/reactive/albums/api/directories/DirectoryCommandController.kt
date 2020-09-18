package nl.juraji.reactive.albums.api.directories

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.projections.directories.DirectoryProjection
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class DirectoryCommandController(
        private val directoriesService: DirectoriesService,
) {

    @PostMapping("/api/directories")
    fun registerDirectory(
            @RequestBody dto: RegisterDirectoryDto,
    ): Flux<DirectoryProjection> =
            directoriesService.registerDirectory(location = dto.location, recursive = dto.recursive)

    @DeleteMapping("/api/directories/{directoryId}")
    fun unregisterDirectory(
            @PathVariable("directoryId") directoryId: String,
            @RequestParam("recursive", required = false, defaultValue = "false") recursive: Boolean,
    ): Flux<DirectoryId> =
            directoriesService.unregisterDirectory(directoryId = DirectoryId(directoryId), recursive = recursive)

    @PutMapping("/api/directories/{directoryId}")
    fun updateDirectory(
            @PathVariable("directoryId") directoryId: String,
            @RequestBody update: UpdateDirectoryDto,
    ): Mono<DirectoryProjection> =
            directoriesService.updateDirectory(
                    directoryId = DirectoryId(directoryId),
                    automaticScanEnabled = update.automaticScanEnabled
            )
}

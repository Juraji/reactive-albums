package nl.juraji.reactive.albums.api.directories

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.services.DirectoriesService
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
    ): Flux<DirectoryProjection> = directoriesService.registerDirectory(
            location = dto.location,
            recursive = dto.recursive
    )

    @PutMapping("/api/directories/{directoryId}")
    fun updateDirectory(
            @PathVariable("directoryId") directoryId: String,
            @RequestBody update: UpdateDirectoryDto,
    ): Mono<DirectoryProjection> = directoriesService.updateDirectory(
            directoryId = DirectoryId(directoryId),
            automaticScanEnabled = update.automaticScanEnabled
    )

    @DeleteMapping("/api/directories/{directoryId}")
    fun unregisterDirectory(
            @PathVariable("directoryId") directoryId: String,
            @RequestParam("recursive", required = false, defaultValue = "false") recursive: Boolean,
    ): Mono<List<String>> = directoriesService.unregisterDirectory(
            directoryId = DirectoryId(directoryId),
            recursive = recursive
    )
}

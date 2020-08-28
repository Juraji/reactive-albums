package nl.juraji.reactive.albums.api.directories

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class DirectoryCommandController(
        private val directoriesService: DirectoriesService,
) {

    @PostMapping("/api/directories")
    fun registerDirectory(@RequestBody dto: RegisterDirectoryDto): Mono<Unit> =
            directoriesService.registerDirectory(location = dto.location, recursive = dto.recursive)

    @DeleteMapping("/api/directories")
    fun unregisterDirectory(@RequestBody dto: UnregisterDirectoryDto): Mono<Unit> =
            directoriesService.unregisterDirectory(directoryId = dto.directoryId, recursive = dto.recursive)
}

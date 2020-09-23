package nl.juraji.reactive.albums.api.directories

import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class DirectoriesQueryController(
        private val directoriesQueryService: DirectoriesQueryService
) {

    @GetMapping("/api/directories")
    fun getAllDirectories(): Flux<DirectoryProjection> =
            directoriesQueryService.getAllDirectories()
}

package nl.juraji.reactive.albums.api.directories

import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class DirectoriesQueryService(
        private val directoryRepository: DirectoryRepository,
) {
    fun getAllDirectories(): Flux<DirectoryProjection> =
            directoryRepository.findAll()
}

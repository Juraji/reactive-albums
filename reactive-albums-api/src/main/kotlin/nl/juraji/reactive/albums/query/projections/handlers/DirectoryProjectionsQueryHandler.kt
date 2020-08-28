package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryRepository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service

@Service
class DirectoryProjectionsQueryHandler(
        private val directoryRepository: DirectoryRepository,
) {

    @QueryHandler
    fun query(q: FindDirectoryById): DirectoryProjection =
            directoryRepository.findById(q.directoryId.identifier)
                    .orElseThrow { NoSuchEntityException("Directory", q.directoryId) }
}

/**
 * Possible results:
 * - DirectoryProjection::class.java
 */
class FindDirectoryById(val directoryId: DirectoryId)

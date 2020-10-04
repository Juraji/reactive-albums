package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.DirectoryTagLUTProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DirectoryTagLUTRepository : JpaRepository<DirectoryTagLUTProjection, String>

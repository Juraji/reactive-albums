package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DirectoryRepository : JpaRepository<DirectoryProjection, String> {
    fun existsByLocation(location: String): Boolean

    @Query("select d from DirectoryProjection d where d.location like concat(:parentLocation, '%')")
    fun findAllSubdirectoriesByLocation(@Param("parentLocation") parentLocation: String): List<DirectoryProjection>
}

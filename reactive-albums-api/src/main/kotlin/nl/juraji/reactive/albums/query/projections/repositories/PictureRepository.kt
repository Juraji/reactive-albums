package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.query.projections.PictureLocationProjection
import nl.juraji.reactive.albums.query.projections.PictureImageProjection
import nl.juraji.reactive.albums.query.projections.PictureProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PictureRepository : JpaRepository<PictureProjection, String> {
    fun existsByLocation(location: String): Boolean
    fun findPictureImageById(id: String): Optional<PictureImageProjection>

    @Query("select p.id from PictureProjection p where p.location like concat(:directory, '%')")
    fun findAllByDirectory(@Param("directory")directory: String): List<PictureLocationProjection>
}

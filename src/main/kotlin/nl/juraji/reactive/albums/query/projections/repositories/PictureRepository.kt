package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.query.projections.PictureImage
import nl.juraji.reactive.albums.query.projections.PictureProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PictureRepository : JpaRepository<PictureProjection, String> {
    fun existsByLocation(location: String): Boolean
    fun findPictureImageById(id: String): Optional<PictureImage>
    fun findAllByPictureType(type: PictureType): List<PictureProjection>
    fun findAllByDisplayNameContainsIgnoreCase(name: String): List<PictureProjection>
}

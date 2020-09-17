package nl.juraji.reactive.albums.query.projections

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DuplicateMatchProjection(
        @Id val id: String,
        val pictureId: String,
        val targetId: String,
        val similarity: Int,
        @Transient val picture: PictureProjection? = null,
        @Transient val target: PictureProjection? = null,
)

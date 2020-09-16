package nl.juraji.reactive.albums.query.projections

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DuplicateMatchProjection(
        @Id val id: String,
        val pictureId: String,
        val targetId: String,
        val similarity: Int,
)

data class DuplicateMatchView(
        val id: String,
        val pictureId: String,
        val targetId: String,
        val similarity: Int,
        val target: PictureProjection
)

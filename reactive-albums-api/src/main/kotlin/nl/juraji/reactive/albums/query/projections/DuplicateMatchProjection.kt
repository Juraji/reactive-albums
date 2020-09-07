package nl.juraji.reactive.albums.query.projections

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DuplicateMatchProjection(
        @Id val id: String,
        val sourceId: String,
        val targetId: String,
        val similarity: Float,
)

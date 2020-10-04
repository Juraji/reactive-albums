package nl.juraji.reactive.albums.query.projections

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ColorTagLUTProjection(
        @Id val tagId: String,
        val red: Int,
        val green: Int,
        val blue: Int,
)

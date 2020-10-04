package nl.juraji.reactive.albums.query.projections

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class DirectoryTagLUTProjection(
        @Id val directoryId: String,
        val tagId: String,
)

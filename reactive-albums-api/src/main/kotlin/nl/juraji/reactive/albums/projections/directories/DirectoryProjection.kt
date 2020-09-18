package nl.juraji.reactive.albums.projections.directories

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DirectoryProjection(
        @Id val id: String,
        val location: String,
        val displayName: String,
        val automaticScanEnabled: Boolean,
)

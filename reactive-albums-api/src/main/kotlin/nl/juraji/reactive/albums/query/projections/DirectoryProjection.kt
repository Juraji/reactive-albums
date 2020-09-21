package nl.juraji.reactive.albums.query.projections

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DirectoryProjection(
        @Id val id: String,
        val location: String,
        val displayName: String,
        val automaticScanEnabled: Boolean,
) : AuditedProjection()

package nl.juraji.reactive.albums.query.projections

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DirectoryProjection(
        @Id val id: String,
        val location: String,
        val displayName: String,
        val automaticScanEnabled: Boolean,
        override var createdAt: LocalDateTime? = null,
        override var lastModifiedAt: LocalDateTime? = null,
) : AuditedProjection(createdAt, lastModifiedAt)

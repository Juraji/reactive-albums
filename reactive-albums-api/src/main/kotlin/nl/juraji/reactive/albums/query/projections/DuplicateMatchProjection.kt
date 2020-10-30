package nl.juraji.reactive.albums.query.projections

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DuplicateMatchProjection(
        @Id val id: String,
        val pictureId: String,
        val targetId: String,
        val similarity: Int,
        val pictureDisplayName: String,
        val targetDisplayName: String,
        override var createdAt: LocalDateTime? = null,
        override var lastModifiedAt: LocalDateTime? = null,
): AuditedProjection(createdAt, lastModifiedAt)

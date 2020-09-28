package nl.juraji.reactive.albums.query.projections

import nl.juraji.reactive.albums.domain.tags.TagType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class TagProjection(
        @Id val id: String,
        @Column(unique = true) val label: String,
        val tagColor: String,
        val textColor: String,
        @Enumerated(EnumType.STRING) val tagType: TagType,
        override var createdAt: LocalDateTime? = null,
        override var lastModifiedAt: LocalDateTime? = null,
) : AuditedProjection(createdAt, lastModifiedAt)

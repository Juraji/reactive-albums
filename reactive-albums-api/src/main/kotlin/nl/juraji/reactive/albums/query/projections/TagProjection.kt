package nl.juraji.reactive.albums.query.projections

import com.fasterxml.jackson.annotation.JsonIgnore
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
        @JsonIgnore val tagColorRed: Int,
        @JsonIgnore val tagColorGreen: Int,
        @JsonIgnore val tagColorBlue: Int,
        override var createdAt: LocalDateTime? = null,
        override var lastModifiedAt: LocalDateTime? = null,
) : AuditedProjection(createdAt, lastModifiedAt)

package nl.juraji.reactive.albums.query.projections

import nl.juraji.reactive.albums.domain.pictures.PictureType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class PictureProjection(
        @Id val id: String,
        val displayName: String,
        val location: String,
        val parentLocation: String,
        @Enumerated(EnumType.STRING) val pictureType: PictureType,
        val analysisCompleted: Boolean = false,
        val fileSize: Long? = null,
        val lastModifiedTime: LocalDateTime? = null,
        val imageWidth: Int? = null,
        val imageHeight: Int? = null,
        val duplicateCount: Int = 0,
        override var createdAt: LocalDateTime? = null,
        override var lastModifiedAt: LocalDateTime? = null,

        @OneToMany(fetch = FetchType.EAGER)
        val tags: Set<TagProjection> = emptySet(),
) : AuditedProjection(createdAt, lastModifiedAt)

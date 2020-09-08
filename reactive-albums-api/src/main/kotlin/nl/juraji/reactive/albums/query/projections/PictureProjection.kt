package nl.juraji.reactive.albums.query.projections

import nl.juraji.reactive.albums.domain.pictures.PictureType
import java.time.LocalDateTime
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id

@Entity
data class PictureProjection(
        @Id val id: String,
        val directoryId: String,
        val displayName: String,
        val location: String,
        val parentLocation: String,
        val pictureType: PictureType,
        val fileSize: Long? = null,
        val lastModifiedTime: LocalDateTime? = null,
        val imageWidth: Int? = null,
        val imageHeight: Int? = null,
        val duplicateCount: Int = 0,

        @ElementCollection(fetch = FetchType.EAGER)
        val tags: Set<TagProjection> = emptySet(),
)

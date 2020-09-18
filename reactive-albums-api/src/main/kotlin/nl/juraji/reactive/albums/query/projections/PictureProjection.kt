package nl.juraji.reactive.albums.query.projections

import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class PictureProjection(
        @Id val id: String,
        val directoryId: String,
        val displayName: String,
        val location: String,
        val parentLocation: String,
        @Enumerated(EnumType.STRING) val pictureType: PictureType,
        val fileSize: Long? = null,
        val lastModifiedTime: LocalDateTime? = null,
        val imageWidth: Int? = null,
        val imageHeight: Int? = null,
        val duplicateCount: Int = 0,

        @ElementCollection(fetch = FetchType.EAGER)
        val tags: Set<TagLink> = emptySet(),
)

@Embeddable
data class TagLink(
        val linkType: TagLinkType,
        @ManyToOne(fetch = FetchType.EAGER)
        val tag: TagProjection,
)

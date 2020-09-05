package nl.juraji.reactive.albums.query.projections

import com.fasterxml.jackson.annotation.JsonIgnore
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.util.serialization.BitsetAttributeConverter
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Converts(
        Convert(attributeName = "contentHash", converter = BitsetAttributeConverter::class)
)
@Entity
data class PictureProjection(
        @Id val id: String,
        val displayName: String,
        val location: String,
        val parentLocation: String,
        val pictureType: PictureType,
        val fileSize: Long? = null,
        val lastModifiedTime: LocalDateTime? = null,
        val imageWidth: Int? = null,
        val imageHeight: Int? = null,
        val duplicateCount: Int = 0,
        @JsonIgnore val thumbnailLocation: String? = null,
        @JsonIgnore val thumbnailType: PictureType? = null,
        @JsonIgnore @Lob val contentHash: BitSet? = null,
        @ElementCollection(fetch = FetchType.EAGER) val tags: Set<TagProjection> = emptySet(),
)

interface PictureImageProjection {
    val location: String
    val pictureType: PictureType
}

interface PictureThumbnailProjection {
    val thumbnailLocation: String?
    val thumbnailType: PictureType?
}

interface PictureLocationProjection {
    val id: String
    val location: String
    val parentLocation: String
}

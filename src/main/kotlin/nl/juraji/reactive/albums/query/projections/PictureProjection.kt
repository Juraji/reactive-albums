package nl.juraji.reactive.albums.query.projections

import com.fasterxml.jackson.annotation.JsonIgnore
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
        val pictureType: PictureType,
        val fileSize: Long? = null,
        val lastModifiedTime: LocalDateTime? = null,
        val imageWidth: Int? = null,
        val imageHeight: Int? = null,
        val duplicateCount: Int = 0,
        @JsonIgnore @Lob val contentHash: BitSet? = null,
        @ElementCollection(fetch = FetchType.EAGER) val tags: Set<TagProjection> = emptySet()
)

data class PictureImage(
        val location: String,
        val pictureType: PictureType,
)

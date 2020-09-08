package nl.juraji.reactive.albums.query.projections

import nl.juraji.reactive.albums.util.serialization.BitsetAttributeConverter
import java.util.*
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob

@Entity
data class ContentHashProjection(
        @Id val pictureId: String,

        @Convert(attributeName = "contentHash", converter = BitsetAttributeConverter::class)
        @Lob val contentHash: BitSet,
)

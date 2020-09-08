package nl.juraji.reactive.albums.query.thumbnails

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob

@Entity
data class Thumbnail(
        @Id val id: String,
        @Lob val thumbnail: ByteArray,
        val lastModifiedTime: LocalDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Thumbnail) return false

        if (id != other.id) return false
        if (!thumbnail.contentEquals(other.thumbnail)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + thumbnail.contentHashCode()
        return result
    }
}

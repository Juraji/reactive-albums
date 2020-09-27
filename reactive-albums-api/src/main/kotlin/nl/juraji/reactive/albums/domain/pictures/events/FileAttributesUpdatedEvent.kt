package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.serialization.Revision
import java.time.LocalDateTime

@Revision("1.0")
data class FileAttributesUpdatedEvent(
        override val pictureId: PictureId,
        val fileSize: Long?,
        val lastModifiedTime: LocalDateTime?,
        val imageWidth: Int?,
        val imageHeight: Int?,
) : PictureEvent

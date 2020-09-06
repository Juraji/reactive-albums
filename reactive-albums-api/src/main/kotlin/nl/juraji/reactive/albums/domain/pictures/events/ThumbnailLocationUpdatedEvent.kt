package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.serialization.Revision
import java.nio.file.Path

@Revision("1.0")
data class ThumbnailLocationUpdatedEvent(
        override val pictureId: PictureId,
        val thumbnailLocation: Path,
) : PictureEvent

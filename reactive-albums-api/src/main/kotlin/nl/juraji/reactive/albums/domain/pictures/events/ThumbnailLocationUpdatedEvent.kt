package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.PictureType
import java.nio.file.Path

data class ThumbnailLocationUpdatedEvent(
        override val pictureId: PictureId,
        val thumbnailLocation: Path,
        val thumbnailType: PictureType,
) : PictureEvent

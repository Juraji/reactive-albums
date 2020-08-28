package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.PictureType
import org.axonframework.serialization.Revision
import java.nio.file.Path

@Revision("1.0")
data class PictureCreatedEvent(
        override val pictureId: PictureId,
        val displayName: String,
        val location: Path,
        val pictureType: PictureType,
) : PictureEvent

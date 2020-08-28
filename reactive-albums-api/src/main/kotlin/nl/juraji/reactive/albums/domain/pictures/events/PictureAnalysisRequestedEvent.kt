package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.serialization.Revision
import java.nio.file.Path

@Revision("1.0")
data class PictureAnalysisRequestedEvent(
        override val pictureId: PictureId,
        val location: Path,
) : PictureEvent

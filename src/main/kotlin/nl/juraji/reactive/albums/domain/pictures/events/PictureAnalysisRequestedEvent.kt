package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import java.nio.file.Path

data class PictureAnalysisRequestedEvent(
        override val pictureId: PictureId,
        val location: Path,
) : PictureEvent

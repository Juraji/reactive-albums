package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureAnalysisStatus
import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.serialization.Revision

@Revision("1.0")
data class PictureAnalysisProgressEvent(
        override val pictureId: PictureId,
        val status: PictureAnalysisStatus,
) : PictureEvent

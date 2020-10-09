package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.serialization.Revision

@Revision("1.0")
data class DuplicateLinkedEvent(
        override val pictureId: PictureId,
        val targetId: PictureId,
        val similarity: Float
) : PictureEvent

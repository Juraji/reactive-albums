package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import java.util.*

data class ContentHashUpdatedEvent(
        override val pictureId: PictureId,
        val contentHash: BitSet,
) : PictureEvent

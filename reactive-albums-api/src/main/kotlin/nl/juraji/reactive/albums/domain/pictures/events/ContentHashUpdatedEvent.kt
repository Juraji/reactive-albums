package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class ContentHashUpdatedEvent(
        override val pictureId: PictureId,
        val contentHash: BitSet,
) : PictureEvent

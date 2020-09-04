package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import org.axonframework.serialization.Revision

@Revision("1.0")
data class TagAddedEvent(
        override val pictureId: PictureId,
        val label: String,
        val labelColor: String,
        val textColor: String,
        val linkType: TagLinkType,
) : PictureEvent

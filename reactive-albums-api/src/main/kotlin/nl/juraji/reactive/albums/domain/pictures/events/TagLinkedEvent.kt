package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.tags.TagId
import org.axonframework.serialization.Revision

@Revision("1.0")
data class TagLinkedEvent(
        override val pictureId: PictureId,
        val tagId: TagId,
) : PictureEvent

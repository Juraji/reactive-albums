package nl.juraji.reactive.albums.domain.tags.events

import nl.juraji.reactive.albums.domain.tags.TagId
import org.axonframework.serialization.Revision

@Revision("1.0")
data class TagDeletedEvent(
        override val tagId: TagId,
) : TagEvent

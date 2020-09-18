package nl.juraji.reactive.albums.domain.tags.events

import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.util.RgbColor

data class TagDeletedEvent(
        override val tagId: TagId,
) : TagEvent

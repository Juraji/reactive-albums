package nl.juraji.reactive.albums.domain.tags.events

import nl.juraji.reactive.albums.domain.tags.TagId

interface TagEvent {
    val tagId: TagId
}

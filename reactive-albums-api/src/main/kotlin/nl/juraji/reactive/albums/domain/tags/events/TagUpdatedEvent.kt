package nl.juraji.reactive.albums.domain.tags.events

import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.util.RgbColor
import org.axonframework.serialization.Revision

@Revision("1.0")
data class TagUpdatedEvent(
        override val tagId: TagId,
        val label: String?,
        val tagColor: RgbColor?,
        val textColor: RgbColor?,
) : TagEvent

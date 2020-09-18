package nl.juraji.reactive.albums.domain.tags.commands

import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.util.RgbColor

data class UpdateTagCommand(
        override val tagId: TagId,
        val label: String? = null,
        val tagColor: RgbColor? = null,
        val textColor: RgbColor? = null,
) : TagCommand(tagId)

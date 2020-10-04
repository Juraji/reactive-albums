package nl.juraji.reactive.albums.domain.tags.commands

import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.util.RgbColor

data class CreateTagCommand(
        override val tagId: TagId,
        val label: String,
        val tagColor: RgbColor? = null,
        val textColor: RgbColor? = null,
        val tagType: TagType = TagType.USER,
        val metaData: Map<String, String> = emptyMap(),
) : TagCommand(tagId) {
    companion object{
        const val META_DIRECTORY_ID = "DIRECTORY_ID"
    }
}

package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.TagLinkType

data class AddTagCommand(
        override val pictureId: PictureId,
        val label: String,
        val color: String,
        val tagLinkType: TagLinkType
): PictureCommand(pictureId)

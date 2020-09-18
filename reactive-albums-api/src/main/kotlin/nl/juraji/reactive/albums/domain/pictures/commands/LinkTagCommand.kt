package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.tags.TagId

data class LinkTagCommand(
        override val pictureId: PictureId,
        val tagId: TagId,
        val tagLinkType: TagLinkType,
) : PictureCommand(pictureId)
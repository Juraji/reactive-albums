package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.tags.TagId

data class UnlinkTagCommand(
        override val pictureId: PictureId,
        val tagId: TagId,
) : PictureCommand(pictureId)

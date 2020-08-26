package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId

data class RemoveTagCommand(
        override val pictureId: PictureId,
        val label: String,
) : PictureCommand(pictureId)

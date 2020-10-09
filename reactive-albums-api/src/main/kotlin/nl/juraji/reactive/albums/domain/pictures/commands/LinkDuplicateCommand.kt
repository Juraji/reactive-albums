package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId

data class LinkDuplicateCommand(
        override val pictureId: PictureId,
        val targetId: PictureId,
        val similarity: Float
) : PictureCommand(pictureId)

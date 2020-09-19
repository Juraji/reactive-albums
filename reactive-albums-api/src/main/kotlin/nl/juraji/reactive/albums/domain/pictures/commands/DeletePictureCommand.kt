package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId

data class DeletePictureCommand(
        override val pictureId: PictureId,
        val deletePhysicalFile: Boolean = false,
) : PictureCommand(pictureId)

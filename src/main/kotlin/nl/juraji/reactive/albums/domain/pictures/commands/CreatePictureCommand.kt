package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import java.nio.file.Path

data class CreatePictureCommand(
        override val pictureId: PictureId,
        val location: Path,
        val contentType: String,
        val displayName: String?,
) : PictureCommand(pictureId)

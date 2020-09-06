package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import java.nio.file.Path

data class UpdateThumbnailLocationCommand(
        override val pictureId: PictureId,
        val thumbnailLocation: Path,
) : PictureCommand(pictureId)

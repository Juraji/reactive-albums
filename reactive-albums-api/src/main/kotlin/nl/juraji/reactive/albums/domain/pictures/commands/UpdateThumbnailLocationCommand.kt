package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.PictureType
import java.nio.file.Path

data class UpdateThumbnailLocationCommand(
        override val pictureId: PictureId,
        val thumbnailLocation: Path,
        val thumbnailType: PictureType,
) : PictureCommand(pictureId)

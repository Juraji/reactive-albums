package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import java.nio.file.Path

data class AnalyzePictureMetaDataCommand(
        override val pictureId: PictureId,
        val pictureLocation: Path,
) : PictureCommand(pictureId)

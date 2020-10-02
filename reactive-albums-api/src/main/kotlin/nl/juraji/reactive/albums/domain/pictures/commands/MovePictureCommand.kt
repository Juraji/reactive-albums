package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import java.nio.file.Path

data class MovePictureCommand(
        override val pictureId: PictureId,
        val targetDirectoryId: DirectoryId,
        val targetLocation: Path,
) : PictureCommand(pictureId)

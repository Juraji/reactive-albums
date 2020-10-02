package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.serialization.Revision
import java.nio.file.Path

@Revision("1.0")
data class PictureMovedEvent(
        override val pictureId: PictureId,
        val sourceDirectoryId: DirectoryId,
        val sourceLocation: Path,
        val targetDirectoryId: DirectoryId,
        val targetLocation: Path,
) : PictureEvent

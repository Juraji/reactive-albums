package nl.juraji.reactive.albums.domain.directories.events

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import org.axonframework.serialization.Revision
import java.nio.file.Path

@Revision("1.0")
data class DirectoryScanRequestedEvent(
        override val directoryId: DirectoryId,
        val location: Path,
        val firstTime: Boolean = false
): DirectoryEvent

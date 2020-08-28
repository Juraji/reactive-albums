package nl.juraji.reactive.albums.domain.directories.events

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import org.axonframework.serialization.Revision
import java.nio.file.Path

@Revision("1.0")
data class DirectoryRegisteredEvent(
        override val directoryId: DirectoryId,
        val location: Path,
        val displayName: String,
) : DirectoryEvent

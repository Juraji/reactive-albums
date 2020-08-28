package nl.juraji.reactive.albums.domain.directories.events

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import org.axonframework.serialization.Revision

@Revision("1.0")
data class DirectoryUnregisteredEvent(
        override val directoryId: DirectoryId,
) : DirectoryEvent

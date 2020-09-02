package nl.juraji.reactive.albums.domain.directories.events

import nl.juraji.reactive.albums.domain.directories.DirectoryId

class DirectoryUpdatedEvent(
        override val directoryId: DirectoryId,
        val automaticScanEnabled: Boolean,
) : DirectoryEvent

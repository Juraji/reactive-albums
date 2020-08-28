package nl.juraji.reactive.albums.domain.directories.events

import nl.juraji.reactive.albums.domain.directories.DirectoryId

interface DirectoryEvent {
    val directoryId: DirectoryId
}

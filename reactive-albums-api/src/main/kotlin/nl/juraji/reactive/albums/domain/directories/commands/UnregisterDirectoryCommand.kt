package nl.juraji.reactive.albums.domain.directories.commands

import nl.juraji.reactive.albums.domain.directories.DirectoryId

data class UnregisterDirectoryCommand(
        override val directoryId: DirectoryId
) : DirectoryCommand(directoryId)

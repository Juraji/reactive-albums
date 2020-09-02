package nl.juraji.reactive.albums.domain.directories.commands

import nl.juraji.reactive.albums.domain.directories.DirectoryId

data class UpdateDirectoryCommand(
        override val directoryId: DirectoryId,
        val automaticScanEnabled: Boolean?,
) : DirectoryCommand(directoryId)

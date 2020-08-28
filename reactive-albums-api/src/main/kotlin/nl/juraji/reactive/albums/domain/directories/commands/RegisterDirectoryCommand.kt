package nl.juraji.reactive.albums.domain.directories.commands

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import java.nio.file.Path

data class RegisterDirectoryCommand(
        override val directoryId: DirectoryId,
        val location: Path
) : DirectoryCommand(directoryId)

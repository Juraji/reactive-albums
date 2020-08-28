package nl.juraji.reactive.albums.api.directories

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import java.nio.file.Path

data class RegisterDirectoryDto(
        val location: Path,
        val recursive: Boolean
)

data class UnregisterDirectoryDto(
        val directoryId: DirectoryId,
        val recursive: Boolean
)

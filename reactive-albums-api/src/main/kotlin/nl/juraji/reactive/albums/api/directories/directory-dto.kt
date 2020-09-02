package nl.juraji.reactive.albums.api.directories

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import java.nio.file.Path

data class RegisterDirectoryDto(
        val location: Path,
        val recursive: Boolean
)

data class UpdateDirectoryDto(
        val directoryId: DirectoryId,
        val automaticScanEnabled: Boolean?,
)

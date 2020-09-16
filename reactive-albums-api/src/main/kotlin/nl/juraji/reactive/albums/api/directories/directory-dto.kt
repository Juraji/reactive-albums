package nl.juraji.reactive.albums.api.directories

import java.nio.file.Path

data class RegisterDirectoryDto(
        val location: Path,
        val recursive: Boolean
)

data class UpdateDirectoryDto(
        val automaticScanEnabled: Boolean?,
)

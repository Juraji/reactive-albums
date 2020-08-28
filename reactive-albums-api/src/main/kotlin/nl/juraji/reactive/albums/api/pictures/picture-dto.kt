package nl.juraji.reactive.albums.api.pictures

import java.nio.file.Path

data class PictureDto(
        val location: Path,
        val displayName: String?,
)

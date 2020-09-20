package nl.juraji.reactive.albums.api.tags

data class CreateTagDto(
        val label: String,
        val tagColor: String?,
        val textColor: String?,
)

data class UpdateTagDto(
        val label: String?,
        val tagColor: String?,
        val textColor: String?,
)

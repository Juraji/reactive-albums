package nl.juraji.reactive.albums.api

import nl.juraji.reactive.albums.domain.Validate
import org.springframework.core.convert.converter.Converter
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class SortParameterConverter : Converter<String, Sort> {
    override fun convert(parameter: String): Sort? {
        if (parameter.isBlank()) {
            return null
        }

        val split: List<String> = parameter.split(',').map { it.trim() }
        val (direction, properties) = when (split.last()) {
            "desc" -> Sort.Direction.DESC to split.dropLast(1)
            "asc" -> Sort.Direction.ASC to split.dropLast(1)
            else -> Sort.DEFAULT_DIRECTION to split
        }

        Validate.isTrue(properties.isNotEmpty()) { "Received sort direction but missing properties" }

        return Sort.by(direction, *properties.toTypedArray())
    }
}

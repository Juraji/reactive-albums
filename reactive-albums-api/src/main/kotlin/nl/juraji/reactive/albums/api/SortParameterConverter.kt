package nl.juraji.reactive.albums.api

import nl.juraji.reactive.albums.domain.Validate
import org.springframework.core.convert.converter.Converter
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class SortParameterConverter : Converter<String, Sort> {
    override fun convert(parameter: String): Sort? {
        val split: List<String> = parameter.split(',')
        if (split.isNotEmpty()) {
            var direction: Sort.Direction = Sort.DEFAULT_DIRECTION
            var properties: List<String> = parameter.split(',')

            when (split.last()) {
                "desc" -> {
                    direction = Sort.Direction.DESC
                    properties = split.dropLast(1)
                }
                "asc" -> {
                    direction = Sort.Direction.ASC
                    properties = split.dropLast(1)
                }
            }

            Validate.isTrue(properties.isNotEmpty()) { "Received sort direction but missing properties" }
            return Sort.by(direction, *properties.toTypedArray())
        }

        return null
    }
}

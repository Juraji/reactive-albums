package nl.juraji.reactive.albums.api.directories


import nl.juraji.reactive.albums.domain.directories.DirectoryId
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class DirectoryIdParameterConverter : Converter<String, DirectoryId> {
    override fun convert(value: String): DirectoryId = DirectoryId(value)
}

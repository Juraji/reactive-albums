package nl.juraji.reactive.albums.api.pictures


import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class PictureIdParameterConverter : Converter<String, PictureId> {
    override fun convert(value: String): PictureId = PictureId(value)
}

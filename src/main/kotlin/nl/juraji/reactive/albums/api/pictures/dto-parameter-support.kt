package nl.juraji.reactive.albums.api.pictures


import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.query.projections.handlers.PictureSearchParameter
import nl.juraji.reactive.albums.query.projections.handlers.PictureSearchParameter.Companion.SEARCH_TYPE_DELIMITER
import nl.juraji.reactive.albums.query.projections.handlers.SearchType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class PictureSearchParameterConverter : Converter<String, PictureSearchParameter> {
    override fun convert(value: String): PictureSearchParameter? = when {
        value.isBlank() -> null
        value.contains(SEARCH_TYPE_DELIMITER) -> extractDelimitedParameter(value)
        else -> PictureSearchParameter(SearchType.DISPLAY_NAME, value)
    }

    private fun extractDelimitedParameter(value: String): PictureSearchParameter {
        return value.split(SEARCH_TYPE_DELIMITER, limit = 2).let { (type, term) ->
            SearchType.values().firstOrNull { it.selector == type }
                    ?.let { searchType -> PictureSearchParameter(searchType, term) }
                    ?: PictureSearchParameter(SearchType.DISPLAY_NAME, value)
        }
    }
}

@Component
class PictureIdParameterConverter : Converter<String, PictureId> {
    override fun convert(value: String): PictureId = PictureId(value)
}

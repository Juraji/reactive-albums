package nl.juraji.reactive.albums.api

import nl.juraji.reactive.albums.domain.ValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Sort

internal class SortParameterConverterTest {

    private val sortParameterConverter = SortParameterConverter()

    @Test
    internal fun `should return null when input is empty`() {
        val sort: Sort? = sortParameterConverter.convert("")

        assertNull(sort)
    }

    @Test
    internal fun `should fail if no properties were given`() {
        assertThrows<ValidationException> {
            sortParameterConverter.convert("asc")
        }
    }

    @Test
    internal fun `should sort by given direction if last parameter is asc or desc else default`() {
        val sortAsc: Sort? = sortParameterConverter.convert("property, asc")
        val sortDesc: Sort? = sortParameterConverter.convert("property, desc")
        val sortDefault: Sort? = sortParameterConverter.convert("property")

        assertEquals(Sort.by(Sort.Direction.ASC, "property"), sortAsc)
        assertEquals(Sort.by(Sort.Direction.DESC, "property"), sortDesc)
        assertEquals(Sort.by("property"), sortDefault)
    }

    @Test
    internal fun `should support multiple properties with and without sort`() {
        val withSort: Sort? = sortParameterConverter.convert("property, otherProperty, desc")
        val withoutSort: Sort? = sortParameterConverter.convert("property, otherProperty")

        assertEquals(Sort.by(Sort.Direction.DESC, "property", "otherProperty"), withSort)
        assertEquals(Sort.by("property", "otherProperty"), withoutSort)
    }
}

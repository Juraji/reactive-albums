package nl.juraji.reactive.albums.util.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SequenceExtKtTest {

    @Test
    internal fun `coordinateSequence should generate a sequence of pairs representing coordinates on a 2d plane`() {
        val expectedSequence: List<Pair<Int, Int>> = listOf(
                0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0,
                0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1,
                0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2,
                0 to 3, 1 to 3, 2 to 3, 3 to 3, 4 to 3,
                0 to 4, 1 to 4, 2 to 4, 3 to 4, 4 to 4,
                0 to 5, 1 to 5, 2 to 5, 3 to 5, 4 to 5,
        )
        val coordinateSequence: List<Pair<Int, Int>> = coordinateSequence(5, 6).toList()

        assertEquals(expectedSequence, coordinateSequence)
    }
}

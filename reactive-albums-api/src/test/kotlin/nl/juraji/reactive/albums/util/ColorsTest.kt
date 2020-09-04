package nl.juraji.reactive.albums.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ColorsTest {

    private val c1 = Color(34, 119, 94)
    private val c2 = Color(194, 211, 128)
    private val c3 = Color(29, 195, 194)

    @Test
    fun `Colors#isHexColor should determine if input is a hexadecimal color representation`() {
        assertTrue(Colors.isHexColor("f93779"))
        assertFalse(Colors.isHexColor("fx3779"))
        assertFalse(Colors.isHexColor("Not a color"))
    }

    @Test
    fun `Colors#generateColor should generate colors based on a seed`() {
        assertEquals(Color(249, 172, 189), Colors.generateColor("Some String"))
        assertEquals(Color(249, 55, 121), Colors.generateColor("f93779"))
    }

    @Test
    fun `Colors#contrastColor should propose a WCAG compliant contrast color`() {
        assertEquals(Colors.WHITE, Colors.contrastColor(c1))
        assertEquals(Colors.BLACK, Colors.contrastColor(c2))
        assertEquals(Colors.BLACK, Colors.contrastColor(c3))
    }

    @Test
    fun `Color#luminance should calculate perceptive color luminance between 0 and 1`() {
        assertEquals(0.0, Colors.BLACK.luminance())
        assertEquals(1.0, Colors.WHITE.luminance())
        assertEquals(0.14341884932604732, c1.luminance())
        assertEquals(0.5961637793548558, c2.luminance())
        assertEquals(0.43186466130331186, c3.luminance())
    }

    @Test
    fun `Color#contrast should calculate contrast ratio between two Colors`() {
        assertEquals(3.868376986520946, c1.contrast(Colors.BLACK))
        assertEquals(12.923275587097116, c2.contrast(Colors.BLACK))
        assertEquals(9.637293226066236, c3.contrast(Colors.BLACK))
        assertEquals(5.428633267433045, c1.contrast(Colors.WHITE))
        assertEquals(1.6249750195660044, c2.contrast(Colors.WHITE))
        assertEquals(2.179035078355897, c3.contrast(Colors.WHITE))
    }

    @Test
    fun `Color#toHexString should convert Color to a hexadecimal representation`() {
        assertEquals("22775e", c1.toHexString())
        assertEquals("c2d380", c2.toHexString())
        assertEquals("1dc3c2", c3.toHexString())
    }
}

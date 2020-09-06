package nl.juraji.reactive.albums.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RgbColorTest {

    private val c1 = RgbColor(34, 119, 94)
    private val c2 = RgbColor(194, 211, 128)
    private val c3 = RgbColor(29, 195, 194)

    @Test
    fun `isHexColor should determine if input is a hexadecimal color representation`() {
        assertTrue(RgbColor.isHexColor("f93779"))
        assertFalse(RgbColor.isHexColor("fx3779"))
        assertFalse(RgbColor.isHexColor("Not a color"))
    }

    @Test
    fun `generateColor should generate colors based on a seed`() {
        assertEquals(RgbColor(249, 172, 189), RgbColor.of("Some String"))
        assertEquals(RgbColor(249, 55, 121), RgbColor.of("f93779"))
    }

    @Test
    fun `contrastColor should propose a WCAG compliant contrast color`() {
        assertEquals(RgbColor.WHITE, c1.contrastColor())
        assertEquals(RgbColor.BLACK, c2.contrastColor())
        assertEquals(RgbColor.BLACK, c3.contrastColor())
    }

    @Test
    fun `luminance should calculate perceptive color luminance between 0 and 1`() {
        assertEquals(0.0, RgbColor.BLACK.luminance())
        assertEquals(1.0, RgbColor.WHITE.luminance())
        assertEquals(0.14341884932604732, c1.luminance())
        assertEquals(0.5961637793548558, c2.luminance())
        assertEquals(0.43186466130331186, c3.luminance())
    }

    @Test
    fun `contrast should calculate contrast ratio between two Colors`() {
        assertEquals(3.868376986520946, c1.contrast(RgbColor.BLACK))
        assertEquals(12.923275587097116, c2.contrast(RgbColor.BLACK))
        assertEquals(9.637293226066236, c3.contrast(RgbColor.BLACK))
        assertEquals(5.428633267433045, c1.contrast(RgbColor.WHITE))
        assertEquals(1.6249750195660044, c2.contrast(RgbColor.WHITE))
        assertEquals(2.179035078355897, c3.contrast(RgbColor.WHITE))
    }

    @Test
    fun `toHexString should convert Color to a hexadecimal representation`() {
        assertEquals("22775e", c1.toHexString())
        assertEquals("c2d380", c2.toHexString())
        assertEquals("1dc3c2", c3.toHexString())
    }

    @Test
    fun `toHsl should convert Color to the correct HslColor`() {
        assertEquals(HslColor(162, 0.5555555555555557, 0.3), c1.toHsl())
        assertEquals(HslColor(72, 0.4853801169590642, 0.6647058823529411), c2.toHsl())
        assertEquals(HslColor(180, 0.7410714285714285, 0.4392156862745098), c3.toHsl())
        assertEquals(HslColor(0, 0.0, 0.0), RgbColor(0, 0, 0).toHsl())
        assertEquals(HslColor(0, 0.0, 0.5019607843137255), RgbColor(128, 128, 128).toHsl())
        assertEquals(HslColor(0, 0.0, 1.0), RgbColor(255, 255, 255).toHsl())
    }
}

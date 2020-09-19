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
        assertTrue(RgbColor.isHexColor("#f93779"))
        assertFalse(RgbColor.isHexColor("fx3779"))
        assertFalse(RgbColor.isHexColor("Not a color"))
    }

    @Test
    fun `generateColor should generate colors based on a seed`() {
        assertEquals(RgbColor(249, 172, 189), RgbColor.of("Some String"))
        assertEquals(RgbColor(249, 55, 121), RgbColor.of("f93779"))
        assertEquals(RgbColor(249, 55, 121), RgbColor.of("#f93779"))
    }

    @Test
    fun `contrastColor should propose a WCAG compliant contrast color`() {
        assertEquals(RgbColor.WHITE, c1.contrastColor())
        assertEquals(RgbColor.BLACK, c2.contrastColor())
        assertEquals(RgbColor.BLACK, c3.contrastColor())
    }

    @Test
    fun `luminance should calculate perceptive color luminance between 0 and 1`() {
        assertEquals(0f, RgbColor.BLACK.luminance())
        assertEquals(1f, RgbColor.WHITE.luminance())
        assertEquals(0.14341886f, c1.luminance())
        assertEquals(0.5961638f, c2.luminance())
        assertEquals(0.43186474f, c3.luminance())
    }

    @Test
    fun `contrast should calculate contrast ratio between two Colors`() {
        assertEquals(20.999998f, RgbColor.WHITE.contrast(RgbColor.BLACK))
        assertEquals(3.8683772f, c1.contrast(RgbColor.BLACK))
        assertEquals(12.923276f, c2.contrast(RgbColor.BLACK))
        assertEquals(9.637295f, c3.contrast(RgbColor.BLACK))
        assertEquals(5.4286327f, c1.contrast(RgbColor.WHITE))
        assertEquals(1.624975f, c2.contrast(RgbColor.WHITE))
        assertEquals(2.1790345f, c3.contrast(RgbColor.WHITE))
    }

    @Test
    fun `toHexString should convert Color to a hexadecimal representation`() {
        assertEquals("#22775e", c1.toString())
        assertEquals("#c2d380", c2.toString())
        assertEquals("#1dc3c2", c3.toString())
    }

    @Test
    fun `toHsl should convert Color to the correct HslColor`() {
        assertEquals(HslColor(162, 0.5555555f, 0.3f), c1.toHsl())
        assertEquals(HslColor(72, 0.48538008f, 0.6647059f), c2.toHsl())
        assertEquals(HslColor(180, 0.74107146f, 0.4392157f), c3.toHsl())
        assertEquals(HslColor(0, 0f, 0f), RgbColor(0, 0, 0).toHsl())
        assertEquals(HslColor(0, 0f, 0.5019608f), RgbColor(128, 128, 128).toHsl())
        assertEquals(HslColor(0, 0f, 1f), RgbColor(255, 255, 255).toHsl())
    }
}

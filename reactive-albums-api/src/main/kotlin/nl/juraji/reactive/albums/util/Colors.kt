package nl.juraji.reactive.albums.util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

data class RgbColor(
        val red: Int,
        val green: Int,
        val blue: Int,
) {
    fun luminance(): Float {
        // Formula: http://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
        val cmpLum: (Float) -> Float = { cmp ->
            val p: Float = cmp / 255f
            if (p < 0.03928) p / 12.92f
            else ((p + 0.055f) / 1.055f).pow(2.4f)
        }

        val rLum: Float = cmpLum(red.toFloat()) * 0.2126f
        val gLum: Float = cmpLum(green.toFloat()) * 0.7152f
        val bLum: Float = cmpLum(blue.toFloat()) * 0.0722f

        return rLum + gLum + bLum
    }

    fun contrast(other: RgbColor): Float {
        // Formula: http://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
        val l1: Float = this.luminance() + 0.05f
        val l2: Float = other.luminance() + 0.05f
        val ratio: Float = l1 / l2

        return if (l2 > l1) 1f / ratio
        else ratio
    }

    fun toHsl(): HslColor {
        val r: Float = red.toFloat() / 255f
        val g: Float = green.toFloat() / 255f
        val b: Float = blue.toFloat() / 255f
        val max: Float = max(r, max(g, b))
        val min: Float = min(r, min(g, b))
        val chroma: Float = max - min

        val lightness: Float = (max + min) / 2f

        val hue = when (max) {
            r ->
                if (g >= b) ((g - b) / chroma) * 60
                else ((g - b) / chroma + 6) * 60
            g -> (2 + (b - r) / chroma) * 60
            b -> (4 + (r - g) / chroma) * 60
            else -> throw IllegalStateException("Max should match either red, green or blue")
        }

        val saturation: Float = when {
            lightness == 1f -> 0f
            lightness >= 0.5f -> chroma / (2f - (max + min))
            else -> chroma / (max + min)
        }

        return HslColor(
                if (!hue.isNaN()) hue.roundToInt() else 0,
                if (!saturation.isNaN()) saturation else 0f,
                lightness
        )
    }

    fun contrastColor(): RgbColor {
        val cWhite = this.contrast(WHITE)
        val cBlack = this.contrast(BLACK)
        return if (cWhite > cBlack) WHITE.copy() else BLACK.copy()
    }

    override fun toString(): String {
        val r = Integer.toHexString(red).padStart(2, '0')
        val g = Integer.toHexString(green).padStart(2, '0')
        val b = Integer.toHexString(blue).padStart(2, '0')

        return "#$r$g$b"
    }

    companion object {
        val BLACK = RgbColor(0, 0, 0)
        val WHITE = RgbColor(255, 255, 255)

        fun isHexColor(str: String): Boolean =
                str.isNotBlank() && str.matches(Regex("^#?[a-f0-9]{6}$"))

        fun of(seed: String): RgbColor {
            val seedColor: Int = if (isHexColor(seed)) Integer.valueOf(seed.trimStart('#'), 16) else seed.hashCode()

            return RgbColor(
                    red = seedColor and 0xFF0000 shr 16,
                    green = seedColor and 0xFF00 shr 8,
                    blue = seedColor and 0xFF
            )
        }
    }
}

data class HslColor(
        val hue: Int,
        val saturation: Float,
        val lightness: Float,
)

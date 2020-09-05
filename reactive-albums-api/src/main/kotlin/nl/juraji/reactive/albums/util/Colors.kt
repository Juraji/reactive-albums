package nl.juraji.reactive.albums.util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

object Colors {
    fun isHexColor(str: String): Boolean =
            str.isNotBlank() && str.matches(Regex("^[a-f0-9]{6}$"))

    fun generateColor(seed: String): RgbColor {
        val seedColor: Int = if (isHexColor(seed)) Integer.valueOf(seed, 16) else seed.hashCode()

        return RgbColor(
                red = seedColor and 0xFF0000 shr 16,
                green = seedColor and 0xFF00 shr 8,
                blue = seedColor and 0xFF
        )
    }

    fun contrastColor(color: RgbColor): RgbColor {
        val cWhite = color.contrast(WHITE)
        val cBlack = color.contrast(BLACK)
        return if (cWhite > cBlack) WHITE.copy() else BLACK.copy()
    }

    val BLACK = RgbColor(0, 0, 0)
    val WHITE = RgbColor(255, 255, 255)
}

data class RgbColor(
        val red: Int,
        val green: Int,
        val blue: Int,
) {
    fun luminance(): Double {
        // Formula: http://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
        val cmpLum: (Double) -> Double = { cmp ->
            val p: Double = cmp / 255.0
            if (p < 0.03928) p / 12.92
            else ((p + 0.055) / 1.055).pow(2.4)
        }

        val rLum = cmpLum(red.toDouble()) * 0.2126
        val gLum = cmpLum(green.toDouble()) * 0.7152
        val bLum = cmpLum(blue.toDouble()) * 0.0722

        return rLum + gLum + bLum
    }

    fun contrast(other: RgbColor): Double {
        // Formula: http://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
        val l1: Double = this.luminance() + 0.05
        val l2: Double = other.luminance() + 0.05
        val ratio: Double = l1 / l2

        return if (l2 > l1) 1.0 / ratio
        else ratio
    }

    fun toHexString(): String {
        val r = Integer.toHexString(red).padStart(2, '0')
        val g = Integer.toHexString(green).padStart(2, '0')
        val b = Integer.toHexString(blue).padStart(2, '0')

        return "$r$g$b"
    }

    fun toHsl(): HslColor {
        val r: Double = red.toDouble() / 255.0
        val g: Double = green.toDouble() / 255.0
        val b: Double = blue.toDouble() / 255.0
        val max: Double = max(r, max(g, b))
        val min: Double = min(r, min(g, b))
        val chroma: Double = max - min

        val lightness: Double = (max + min) / 2.0

        val hue = when (max) {
            r ->
                if (g >= b) ((g - b) / chroma) * 60
                else ((g - b) / chroma + 6) * 60
            g -> (2 + (b - r) / chroma) * 60
            b -> (4 + (r - g) / chroma) * 60
            else -> throw IllegalStateException("Max should match either red, green or blue")
        }

        val saturation: Double = when {
            lightness == 1.0 -> 0.0
            lightness >= 0.5 -> chroma / (2.0 - (max + min))
            else -> chroma / (max + min)
        }

        return HslColor(
                if (!hue.isNaN()) hue.roundToInt() else 0,
                if (!saturation.isNaN()) saturation else 0.0,
                lightness
        )
    }
}

data class HslColor(
        val hue: Int,
        val saturation: Double,
        val lightness: Double,
)

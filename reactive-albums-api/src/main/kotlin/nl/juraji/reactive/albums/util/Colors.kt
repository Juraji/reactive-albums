package nl.juraji.reactive.albums.util

import kotlin.math.pow

data class Color(
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

    fun contrast(other: Color): Double {
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
}

object Colors {
    fun isHexColor(str: String): Boolean =
            str.isNotBlank() && str.matches(Regex("^[a-f0-9]{6}$"))

    fun generateColor(seed: String): Color {
        val seedColor: Int = if (isHexColor(seed)) Integer.valueOf(seed, 16) else seed.hashCode()

        return Color(
                red = seedColor and 0xFF0000 shr 16,
                green = seedColor and 0xFF00 shr 8,
                blue = seedColor and 0xFF
        )
    }

    fun contrastColor(color: Color): Color {
        val cWhite = color.contrast(WHITE)
        val cBlack = color.contrast(BLACK)
        return if (cWhite > cBlack) WHITE.copy() else BLACK.copy()
    }

    val BLACK = Color(0, 0, 0)
    val WHITE = Color(255, 255, 255)
}

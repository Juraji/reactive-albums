package nl.juraji.reactive.albums.util.extensions

fun CharSequence.isHexColor(): Boolean = isNotBlank() && matches(Regex("^[a-f0-9]{6}$"))
fun CharSequence.toHexColor(): String =
        if (this.isHexColor()) this.toString()
        else Integer
                .toHexString(this.hashCode())
                .padStart(8, '0')
                .substring(2)

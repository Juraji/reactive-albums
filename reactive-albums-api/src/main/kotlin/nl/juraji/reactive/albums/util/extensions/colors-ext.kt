package nl.juraji.reactive.albums.util.extensions

fun CharSequence.isHexColor(): Boolean = isNotBlank() && matches(Regex("^[a-f0-9]{6}$"))

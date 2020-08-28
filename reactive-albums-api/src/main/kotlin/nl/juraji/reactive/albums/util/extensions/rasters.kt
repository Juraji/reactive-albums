package nl.juraji.reactive.albums.util.extensions

fun indexedCoordinateSequence(width: Int, height: Int): Sequence<Triple<Int, Int, Int>> =
        coordinateSequence(width, height)
                .withIndex()
                .map { (index, coords) -> index to coords.first then coords.second }

fun coordinateSequence(width: Int, height: Int): Sequence<Pair<Int, Int>> {
    val xMax = width - 1
    val yMax = height - 1

    return generateSequence(0 to 0) { (x, y) ->
        when {
            x < xMax -> (x + 1) to y
            y < yMax -> 0 to (y + 1)
            else -> null
        }
    }
}

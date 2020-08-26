package nl.juraji.reactive.albums.util.extensions

infix fun <A, B, C> Pair<A, B>.then(c: C): Triple<A, B, C> = Triple(this.first, this.second, c)

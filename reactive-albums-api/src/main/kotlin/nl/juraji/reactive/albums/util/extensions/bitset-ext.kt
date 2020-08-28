package nl.juraji.reactive.albums.util.extensions

import java.util.*

fun BitSet.compareSimilarity(other: BitSet): Float {
    return if (this == other) 1.0f
    else {
        val xorOp = clone() as BitSet
        xorOp.xor(other)
        xorOp.flip(0, xorOp.length() - 1) // Convert to XNOR
        val bitCount = xorOp.length().toFloat()
        val similarBitcount = xorOp.cardinality().toFloat()
        similarBitcount / bitCount
    }
}

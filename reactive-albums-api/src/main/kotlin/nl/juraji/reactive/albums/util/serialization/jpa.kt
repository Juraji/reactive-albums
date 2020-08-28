package nl.juraji.reactive.albums.util.serialization

import java.util.*
import javax.persistence.AttributeConverter

class BitsetAttributeConverter : AttributeConverter<BitSet, ByteArray> {
    override fun convertToDatabaseColumn(attribute: BitSet?): ByteArray? = attribute?.toByteArray()
    override fun convertToEntityAttribute(dbData: ByteArray?): BitSet? = dbData?.let { BitSet.valueOf(it) }
}

package nl.juraji.reactive.albums.util.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import nl.juraji.reactive.albums.domain.EntityId
import java.util.*

class BitsetDeSerializer : StdDeserializer<BitSet>(BitSet::class.java) {
    override fun deserialize(p: JsonParser, ctx: DeserializationContext): BitSet {
        var longArray = LongArray(0)
        var token: JsonToken = p.nextValue()

        while (JsonToken.END_ARRAY != token) {
            if (token.isNumeric) longArray = longArray.plus(p.longValue)
            token = p.nextValue()
        }

        return BitSet.valueOf(longArray)
    }
}

class BitsetSerializer : StdSerializer<BitSet>(BitSet::class.java) {
    override fun serialize(value: BitSet, gen: JsonGenerator, serializers: SerializerProvider) {
        val longArray = value.toLongArray()

        gen.writeStartArray()
        longArray.forEach { gen.writeNumber(it) }
        gen.writeEndArray()
    }
}

class EntityIdSerializer: StdSerializer<EntityId>(EntityId::class.java) {
    override fun serialize(value: EntityId, gen: JsonGenerator?, serializers: SerializerProvider) {
        serializers.defaultSerializeValue(value.identifier, gen)
    }
}

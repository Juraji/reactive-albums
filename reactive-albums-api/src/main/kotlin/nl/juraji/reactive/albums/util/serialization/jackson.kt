package nl.juraji.reactive.albums.util.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import nl.juraji.reactive.albums.domain.EntityId
import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.tags.TagId
import java.util.*
import kotlin.reflect.KClass

private fun <T : Any> SimpleModule.serializer(type: KClass<T>, block: JsonGenerator.(T, SerializerProvider) -> Unit) {
    this.addSerializer(object : StdSerializer<T>(type.java) {
        override fun serialize(value: T, gen: JsonGenerator, serializers: SerializerProvider) = block(gen, value, serializers)
    })
}

private fun <T : Any> SimpleModule.keySerializer(type: KClass<T>, block: JsonGenerator.(T, SerializerProvider) -> Unit) {
    this.addKeySerializer(type.java, object : StdSerializer<T>(type.java) {
        override fun serialize(value: T, gen: JsonGenerator, serializers: SerializerProvider) = block(gen, value, serializers)
    })
}

private fun <T : Any> SimpleModule.deserializer(type: KClass<T>, block: JsonParser.(DeserializationContext) -> T) {
    this.addDeserializer(type.java, object : StdDeserializer<T>(type.java) {
        override fun deserialize(parser: JsonParser, context: DeserializationContext): T = block(parser, context)
    })
}

private fun SimpleModule.keyDeserializer(type: KClass<*>, block: DeserializationContext.(String) -> Any) {
    this.addKeyDeserializer(type.java, object : KeyDeserializer() {
        override fun deserializeKey(key: String, context: DeserializationContext): Any = block(context, key)
    })
}

private fun simpleModule(block: SimpleModule.() -> Unit): SimpleModule {
    return SimpleModule().apply(block)
}

val bitSetModule = simpleModule {
    serializer(BitSet::class) { value, _ ->
        val longArray = value.toLongArray()

        writeStartArray()
        longArray.forEach { writeNumber(it) }
        writeEndArray()
    }

    deserializer(BitSet::class) {
        var longArray = LongArray(0)
        var token: JsonToken = nextValue()

        while (JsonToken.END_ARRAY != token) {
            if (token.isNumeric) longArray = longArray.plus(longValue)
            token = nextValue()
        }

        BitSet.valueOf(longArray)
    }
}

val entityIdModule = simpleModule {
    serializer(EntityId::class) { value, _ -> writeString(value.identifier) }
    keySerializer(EntityId::class) { value, _ -> writeFieldName(value.identifier) }

    deserializer(PictureId::class) { PictureId(valueAsString) }
    deserializer(DirectoryId::class) { DirectoryId(valueAsString) }
    deserializer(TagId::class) { TagId(valueAsString) }
    deserializer(DuplicateMatchId::class) { DuplicateMatchId(valueAsString) }

    keyDeserializer(PictureId::class) { value -> PictureId(value) }
    keyDeserializer(DirectoryId::class) { value -> DirectoryId(value) }
    keyDeserializer(TagId::class) { value -> TagId(value) }
    keyDeserializer(DuplicateMatchId::class) { value -> DuplicateMatchId(value) }
}

package io.github.sunshinewzy.sunstcore.core.data.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.sunshinewzy.sunstcore.utils.Coerce
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagList
import taboolib.module.nms.ItemTagType.*

object ItemTagDataSerializer : StdSerializer<ItemTagData>(ItemTagData::class.java) {

    override fun serialize(value: ItemTagData, gen: JsonGenerator, provider: SerializerProvider) {
        when(value.type) {
            COMPOUND -> {
                gen.writeStartObject()
                if(value is ItemTag) {
                    value.forEach { (key, data) -> 
                        provider.defaultSerializeField(key, data, gen)
                    }
                }
                gen.writeEndObject()
            }
            LIST -> {
                if(value is ItemTagList) {
                    gen.writeStartArray()
                    value.forEach { data ->
                        provider.defaultSerializeValue(data, gen)
                    }
                    gen.writeEndArray()
                }
            }
            BYTE -> gen.writeString("${value.asByte()}b")
            SHORT -> gen.writeString("${value.asShort()}s")
            INT -> gen.writeString("${value.asInt()}i")
            LONG -> gen.writeString("${value.asLong()}l")
            FLOAT -> gen.writeString("${value.asFloat()}f")
            DOUBLE -> gen.writeString("${value.asDouble()}d")
            STRING, END -> gen.writeString("${value.asString()}t")
            INT_ARRAY -> gen.writeString("${value.asIntArray().joinToString(",") { it.toString() }}i]")
            BYTE_ARRAY -> gen.writeString("${value.asByteArray().joinToString(",") { it.toString() }}b]")
            else -> {}
        }
    }
    
}

object ItemTagDataDeserializer : StdDeserializer<ItemTagData>(ItemTagData::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ItemTagData {
        return when(val node = p.readValueAsTree<JsonNode>()) {
            is ArrayNode -> {
                ItemTagList().also { tagList ->
                    node.forEach { 
                        tagList.add(ctxt.readTreeAsValue(it, ItemTagData::class.java))
                    }
                }
            }
            is ObjectNode -> {
                ItemTag().also { tag ->
                    node.fields().forEach { (key, data) ->
                        tag[key] = ctxt.readTreeAsValue(data, ItemTagData::class.java)
                    }
                }
            }
            is ValueNode -> {
                val str = node.asText()
                if (str.endsWith(']')) {
                    when (val i = str.substring(str.length - 2, str.length - 1)) {
                        "b" -> ItemTagData(str.substring(0, str.length - 2).split(",").map { Coerce.toByte(it) }.toByteArray())
                        "i" -> ItemTagData(str.substring(0, str.length - 2).split(",").map { Coerce.toInteger(it) }.toIntArray())
                        else -> error("unsupported array $node ($i)")
                    }
                } else {
                    when (val i = str.substring(str.length - 1, str.length)) {
                        "n" -> ItemTagData(Coerce.toByte(str.substring(0, str.length - 1)))
                        "s" -> ItemTagData(Coerce.toShort(str.substring(0, str.length - 1)))
                        "i" -> ItemTagData(Coerce.toInteger(str.substring(0, str.length - 1)))
                        "l" -> ItemTagData(Coerce.toLong(str.substring(0, str.length - 1)))
                        "f" -> ItemTagData(Coerce.toFloat(str.substring(0, str.length - 1)))
                        "d" -> ItemTagData(Coerce.toDouble(str.substring(0, str.length - 1)))
                        "t" -> ItemTagData(str.substring(0, str.length - 1))
                        else -> error("unsupported type $node ($i)")
                    }
                }
            }
            else -> error("Unsupported json $node (${node.javaClass.simpleName})")
        }
    }
    
}
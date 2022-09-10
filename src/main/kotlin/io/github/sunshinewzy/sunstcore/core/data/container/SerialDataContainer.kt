package io.github.sunshinewzy.sunstcore.core.data.container

import com.fasterxml.jackson.core.JsonEncoding
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.data.SerialDataRoot
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap


class SerialDataContainer(
    override val objectMapper: ObjectMapper = ObjectMapper()
) : ISerialDataContainer {
    private val map: ConcurrentHashMap<NamespacedId, ISerialDataRoot> = ConcurrentHashMap()
    
    
    override fun get(key: NamespacedId): ISerialDataRoot {
        map[key]?.let { return it }

        return SerialDataRoot(key.id, this).also { map[key] = it }
    }

    override fun serialize(generator: JsonGenerator): JsonGenerator {
        generator.writeStartObject()
        map.forEach { (key, data) -> 
            generator.writeFieldName(key.toString())
            data.serialize(generator)
        }
        generator.writeEndObject()
        return generator
    }

    override fun <T : OutputStream> serialize(stream: T): T {
        objectMapper.createGenerator(stream, JsonEncoding.UTF8).use { generator ->
            serialize(generator)
        }

        return stream
    }

    override fun serializeToJsonNode(): JsonNode {
        val node = objectMapper.createObjectNode()
        map.forEach { (key, data) -> 
            node.replace(key.toString(), data.serializeToJsonNode())
        }
        return node
    }

    override fun serializeToString(): String {
        return ByteArrayOutputStream().use { stream ->
            serialize(stream).toString(Charsets.UTF_8.name())
        }
    }

    override fun deserialize(source: JsonNode): Boolean {
        if(source !is ObjectNode) return false

        source.fields().forEach { (key, node) ->
            NamespacedId.fromString(key)?.let {
                map[it] = ISerialDataRoot.deserialize(node, key, this)
            } ?: return false
        }
        
        return true
    }

    override fun deserialize(source: String): Boolean {
        return deserialize(objectMapper.readTree(source))
    }
}
package io.github.sunshinewzy.sunstcore.core.data.container

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.data.SerialDataRoot
import java.util.concurrent.ConcurrentHashMap


class SerialDataContainer : ISerialDataContainer {
    private val map = ConcurrentHashMap<NamespacedId, ISerialDataRoot>()


    override val objectMapper: ObjectMapper = jacksonObjectMapper()

    
    override fun get(key: NamespacedId): ISerialDataRoot {
        map[key]?.let { return it }

        return SerialDataRoot(key.id, this).also { map[key] = it }
    }

    override fun serializeToJsonNode(): JsonNode {
        val node = objectMapper.createObjectNode()
        map.forEach { (key, data) -> 
            node.replace(key.toString(), data.serializeToJsonNode())
        }
        return node
    }

    override fun serializeToString(): String {
        return serializeToJsonNode().toString()
    }
    
}
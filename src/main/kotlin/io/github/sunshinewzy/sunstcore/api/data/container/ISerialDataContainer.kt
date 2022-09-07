package io.github.sunshinewzy.sunstcore.api.data.container

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.data.container.SerialDataContainer

interface ISerialDataContainer : IDataContainer {
    
    val objectMapper: ObjectMapper
    

    /**
     * Get the requested [ISerialDataRoot] by [key].
     *
     *
     * 通过 [key] 获取一个 [ISerialDataRoot]。
     *
     * @param key Key of the [ISerialDataRoot] to get.
     * @return Requested [ISerialDataRoot].
     */
    override operator fun get(key: NamespacedId): ISerialDataRoot

    @JsonValue
    fun serializeToJsonNode(): JsonNode

    fun serializeToString(): String
    
    fun deserialize(source: JsonNode): Boolean
    
    fun deserialize(source: String): Boolean
    
    
    companion object {
        @JvmStatic
        @JsonCreator
        fun deserialize(source: JsonNode): ISerialDataContainer {
            return SerialDataContainer().also {
                if(!it.deserialize(source)) {
                    throw RuntimeException("Deserialization failed.")
                }
            }
        }
    }
}
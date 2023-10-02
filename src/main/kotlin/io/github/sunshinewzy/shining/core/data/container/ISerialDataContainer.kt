package io.github.sunshinewzy.shining.core.data.container

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.data.ISerialDataRoot
import java.io.OutputStream

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


    fun serialize(generator: JsonGenerator): JsonGenerator

    fun <T : OutputStream> serialize(stream: T): T

    @JsonValue
    fun serializeToJsonNode(): JsonNode

    fun serializeToString(): String

    fun deserialize(source: JsonNode): Boolean

    fun deserialize(source: String): Boolean


    companion object {
        @JvmStatic
        fun deserialize(
            source: JsonNode,
            objectMapper: ObjectMapper = ObjectMapper()
        ): ISerialDataContainer {
            return SerialDataContainer(objectMapper).also {
                if (!it.deserialize(source)) {
                    throw RuntimeException("Deserialization failed.")
                }
            }
        }

        @JvmStatic
        fun deserialize(
            source: String,
            objectMapper: ObjectMapper = ObjectMapper()
        ): ISerialDataContainer {
            return deserialize(objectMapper.readTree(source), objectMapper)
        }
    }
}
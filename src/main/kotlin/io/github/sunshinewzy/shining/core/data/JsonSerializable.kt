package io.github.sunshinewzy.shining.core.data

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonNode
import java.io.OutputStream

interface JsonSerializable {

    fun serialize(generator: JsonGenerator): JsonGenerator

    fun <T : OutputStream> serialize(stream: T): T

    fun serializeToJsonNode(): JsonNode

    @JsonValue
    fun serializeToString(): String

    fun deserialize(source: JsonNode): Boolean

    fun deserialize(source: String): Boolean
    
}
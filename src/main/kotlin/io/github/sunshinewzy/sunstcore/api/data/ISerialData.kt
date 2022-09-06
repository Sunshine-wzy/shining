package io.github.sunshinewzy.sunstcore.api.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

interface ISerialData : IData {
    @get:JsonIgnore
    override val root: ISerialDataRoot
    @get:JsonIgnore
    override val parent: ISerialData?

    @get:JsonIgnore
    val objectMapper: ObjectMapper


    override fun getData(path: String): ISerialData?

    override fun createData(path: String): ISerialData

    @JsonValue
    fun serializeToJsonNode(): JsonNode
    
    fun serializeToString(): String

    fun deserialize(source: JsonNode)
    
    fun deserialize(source: String)
    
}
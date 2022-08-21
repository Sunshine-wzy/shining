package io.github.sunshinewzy.sunstcore.api.data

import kotlinx.serialization.json.JsonElement

interface ISerialData : IData {
    override val parent: ISerialData?

    
    fun serializeToString(): String
    
    fun serializeToJsonElement(): JsonElement
    
    fun deserialize(source: String)
    
}
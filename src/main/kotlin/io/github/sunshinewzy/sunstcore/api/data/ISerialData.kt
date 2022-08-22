package io.github.sunshinewzy.sunstcore.api.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

interface ISerialData : IData {
    override val root: ISerialDataRoot
    
    override val parent: ISerialData?


    val serializersModule: SerializersModule


    override fun getData(path: String): ISerialData?

    override fun createData(path: String): ISerialData

    
    fun <T> set(path: String, value: T, serializer: KSerializer<T>)
    
    
    fun serializeToString(): String
    
    fun serializeToJsonElement(): JsonElement
    
    fun deserialize(source: String)
    
    
    companion object {
        inline fun <reified T> ISerialData.setSerializable(path: String, value: T) {
            set(path, value, serializersModule.serializer())
        }
    }
    
}
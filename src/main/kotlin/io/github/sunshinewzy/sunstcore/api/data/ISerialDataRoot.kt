package io.github.sunshinewzy.sunstcore.api.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer
import io.github.sunshinewzy.sunstcore.core.data.SerialDataRoot

interface ISerialDataRoot : ISerialData, IDataRoot {
    
    @get:JsonIgnore
    override val container: ISerialDataContainer


    @get:JsonIgnore
    val objectMapper: ObjectMapper
    
    
    companion object {
        @JvmStatic
        fun deserialize(
            source: JsonNode,
            name: String,
            container: ISerialDataContainer
        ): ISerialDataRoot {
            return SerialDataRoot(name, container).also {
                if(!it.deserialize(source)) {
                    throw RuntimeException("Deserialization failed.")
                }
            }
        }
    }
}
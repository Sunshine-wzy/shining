package io.github.sunshinewzy.sunstcore.core.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer

class SerialDataRoot(
    name: String,
    override val container: ISerialDataContainer
) : SerialData(name), ISerialDataRoot {

    override var options: DataRootOptions = DataRootOptions()

    override val objectMapper: ObjectMapper = container.objectMapper
    

    override fun serializeToJsonNode(): JsonNode {
        val rootNode = objectMapper.createObjectNode()
        rootNode.putPOJO(KEY_OPTIONS, options)
        rootNode.replace(KEY_DATA, super.serializeToJsonNode())
        return rootNode
    }

    override fun deserialize(source: JsonNode): Boolean {
        if(source !is ObjectNode) return false
        
        this.options = objectMapper.treeToValue(source[KEY_OPTIONS], DataRootOptions::class.java) ?: return false
        source[KEY_DATA]?.let { 
            return super.deserialize(it)
        } ?: return false
    }
    
    
    companion object {
        const val KEY_OPTIONS = "options"
        const val KEY_DATA = "data"
    }
}
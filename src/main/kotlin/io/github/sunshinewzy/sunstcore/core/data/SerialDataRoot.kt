package io.github.sunshinewzy.sunstcore.core.data

import com.fasterxml.jackson.databind.JsonNode
import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer
import io.github.sunshinewzy.sunstcore.core.data.container.DataRootOptions

class SerialDataRoot(
    name: String,
    override val container: ISerialDataContainer
) : SerialData(name), ISerialDataRoot {

    override val options: DataRootOptions = DataRootOptions()

    override fun serializeToJsonNode(): JsonNode {
        val rootNode = objectMapper.createObjectNode()
        rootNode.putPOJO("options", options)
        rootNode.replace("data", super.serializeToJsonNode())
        return rootNode
    }

    override fun deserialize(source: JsonNode) {
        
        super.deserialize(source)
    }
}
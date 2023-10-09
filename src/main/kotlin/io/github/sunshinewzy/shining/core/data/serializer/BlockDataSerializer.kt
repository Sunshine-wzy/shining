package io.github.sunshinewzy.shining.core.data.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.bukkit.Bukkit
import org.bukkit.block.data.BlockData

object BlockDataSerializer : StdSerializer<BlockData>(BlockData::class.java) {
    
    private fun readResolve(): Any = BlockDataSerializer

    override fun serialize(value: BlockData, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.asString)
    }
    
}

object BlockDataDeserializer : StdDeserializer<BlockData>(BlockData::class.java) {
    
    private fun readResolve(): Any = BlockDataDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): BlockData =
        Bukkit.createBlockData(p.text)
    
}
package io.github.sunshinewzy.shining.core.data.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

object ItemStackSerializer : StdSerializer<ItemStack>(ItemStack::class.java) {
    
    private fun readResolve(): Any = ItemStackSerializer

    override fun serialize(value: ItemStack, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("type", value.type.name)
        gen.writeNumberField("amount", value.amount)
        provider.defaultSerializeField("nbt", value.getItemTag(), gen)
        gen.writeEndObject()
    }

}

object ItemStackDeserializer : StdDeserializer<ItemStack>(ItemStack::class.java) {
    
    private fun readResolve(): Any = ItemStackDeserializer

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ItemStack {
        val node = p.readValueAsTree<JsonNode>()
        val item = ItemStack(Material.AIR)
        Material.getMaterial(node["type"].textValue())?.let { type ->
            item.type = type
            item.amount = node["amount"].asInt()
            val tag = ctxt.readTreeAsValue(node["nbt"], ItemTagData::class.java)
            return item.setItemTag(tag.asCompound())
        }

        return item
    }

}
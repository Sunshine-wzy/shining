package io.github.sunshinewzy.sunstcore.core.data.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.bukkit.inventory.ItemStack

object ItemStackSerializer : StdSerializer<ItemStack>(ItemStack::class.java) {

    override fun serialize(value: ItemStack, gen: JsonGenerator, provider: SerializerProvider) {
        provider.defaultSerializeValue(value.serialize(), gen)
    }
    
}

object ItemStackDeserializer : StdDeserializer<ItemStack>(ItemStack::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ItemStack {
        val type = ctxt.typeFactory.constructMapType(LinkedHashMap::class.java, String::class.java, Any::class.java)
        return ItemStack.deserialize(ctxt.readValue(p, type))
    }
    
}
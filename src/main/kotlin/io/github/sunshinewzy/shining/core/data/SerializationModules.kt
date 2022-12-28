package io.github.sunshinewzy.shining.core.data

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.sunshinewzy.shining.core.data.serializer.ItemStackDeserializer
import io.github.sunshinewzy.shining.core.data.serializer.ItemStackSerializer
import io.github.sunshinewzy.shining.core.data.serializer.ItemTagDataDeserializer
import io.github.sunshinewzy.shining.core.data.serializer.ItemTagDataSerializer
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTagData

object SerializationModules {
    val VERSION: Version = Version(2, 0, 0, null, "io.github.sunshinewzy", "Shining")
    
    val bukkit: SimpleModule = SimpleModule("Bukkit", VERSION).apply { 
        addSerializer(ItemStack::class.java, ItemStackSerializer)
        addDeserializer(ItemStack::class.java, ItemStackDeserializer)
        
        addSerializer(ItemTagData::class.java, ItemTagDataSerializer)
        addDeserializer(ItemTagData::class.java, ItemTagDataDeserializer)
    }
    
}
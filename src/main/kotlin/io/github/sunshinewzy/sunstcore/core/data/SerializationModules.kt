package io.github.sunshinewzy.sunstcore.core.data

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.sunshinewzy.sunstcore.core.data.serializer.ItemStackDeserializer
import io.github.sunshinewzy.sunstcore.core.data.serializer.ItemStackSerializer
import org.bukkit.inventory.ItemStack

object SerializationModules {
    val VERSION: Version = Version(2, 0, 0, null, "io.github.sunshinewzy", "SunSTCore")
    
    val bukkit: SimpleModule = SimpleModule("Bukkit", VERSION).apply { 
        addSerializer(ItemStack::class.java, ItemStackSerializer)
        addDeserializer(ItemStack::class.java, ItemStackDeserializer)
    }
    
}
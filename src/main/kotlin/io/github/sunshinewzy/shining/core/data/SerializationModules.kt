package io.github.sunshinewzy.shining.core.data

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.sunshinewzy.shining.core.data.serializer.*
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTagData

object SerializationModules {
    val VERSION: Version = Version(2, 0, 0, null, "io.github.sunshinewzy", "shining")


    val shining: SimpleModule = SimpleModule("Shining", VERSION).apply {
        addSerializer(JacksonWrapper::class.java, JacksonWrapperSerializer)
        addDeserializer(JacksonWrapper::class.java, JacksonWrapperDeserializer())
    }

    val bukkit: SimpleModule = SimpleModule("Bukkit", VERSION).apply {
        addSerializer(ItemStack::class.java, ItemStackSerializer)
        addDeserializer(ItemStack::class.java, ItemStackDeserializer)

        addSerializer(ItemTagData::class.java, ItemTagDataSerializer)
        addDeserializer(ItemTagData::class.java, ItemTagDataDeserializer)
    }

}
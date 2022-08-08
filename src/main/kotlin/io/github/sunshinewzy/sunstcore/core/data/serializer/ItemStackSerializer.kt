package io.github.sunshinewzy.sunstcore.core.data.serializer

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagSerializer
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

object ItemStackSerializer : StringSerializer<ItemStack>("ItemStack") {

    override fun toString(value: ItemStack): String {
        val json = JsonObject()
        json.addProperty("type", value.type.name)
        json.addProperty("amount", value.amount)
        json.add("nbt", ItemTagSerializer.serializeData(value.getItemTag()))
        return json.toString()
    }

    override fun fromString(source: String): ItemStack {
        val item = ItemStack(Material.AIR)
        val json = JsonParser.parseString(source)
        if(json is JsonObject) {
            item.type = Material.valueOf(json["type"].asString)
            item.amount = json["amount"].asInt
            item.setItemTag(ItemTag.fromJson(json["nbt"]).asCompound())
        }
        
        return item
    }
}
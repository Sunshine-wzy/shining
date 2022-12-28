package io.github.sunshinewzy.shining.core.data.serializer.kotlinx

import org.bukkit.inventory.ItemStack

object ItemStackSerializer : StringSerializer<ItemStack>("ItemStack") {

    override fun toString(value: ItemStack): String {
//        val json = JsonObject()
//        json.addProperty("type", value.type.name)
//        json.addProperty("amount", value.amount)
//        json.add("nbt", ItemTagSerializer.serializeData(value.getItemTag()))
//        return json.toString()
        TODO()
    }

    override fun fromString(source: String): ItemStack {
//        val item = ItemStack(Material.AIR)
//        val json = JsonParser.parseString(source)
//        if(json is JsonObject) {
//            item.type = Material.valueOf(json["type"].asString)
//            item.amount = json["amount"].asInt
//            item.setItemTag(ItemTag.fromJson(json["nbt"]).asCompound())
//        }
        
//        return item
        TODO()
    }
}
package io.github.sunshinewzy.shining.api.item.universal

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonValue
import io.github.sunshinewzy.shining.utils.containsItem
import io.github.sunshinewzy.shining.utils.removeSItem
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@JsonTypeName("vanilla")
class VanillaItem @JsonCreator constructor(@JsonValue private val item: ItemStack) : UniversalItem {

    override fun getItemStack(): ItemStack = item

    override fun contains(inventory: Inventory): Boolean =
        inventory.containsItem(item)

    override fun consume(inventory: Inventory): Boolean =
        inventory.removeSItem(item)
    
}
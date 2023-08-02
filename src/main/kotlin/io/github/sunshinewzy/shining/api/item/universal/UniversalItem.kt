package io.github.sunshinewzy.shining.api.item.universal

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
interface UniversalItem {
    
    fun getItemStack(): ItemStack
    
    fun contains(inventory: Inventory): Boolean
    
    fun consume(inventory: Inventory): Boolean
    
}
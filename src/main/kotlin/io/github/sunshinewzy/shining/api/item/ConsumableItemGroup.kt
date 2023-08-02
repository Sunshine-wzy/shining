package io.github.sunshinewzy.shining.api.item

import io.github.sunshinewzy.shining.api.item.universal.UniversalItem
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ConsumableItemGroup(
    var isConsume: Boolean,
    val items: MutableList<UniversalItem> = ArrayList()
) {
    
    constructor() : this(true)
    
    constructor(isConsume: Boolean, item: UniversalItem) : this(isConsume, arrayListOf(item))
    
    
    fun getItemStacks(): MutableList<ItemStack> = items.mapTo(ArrayList()) { it.getItemStack() }
    
    fun contains(inventory: Inventory): Boolean {
        items.forEach { 
            if (!it.contains(inventory)) return false
        }
        return true
    }
    
    fun contains(player: Player): Boolean =
        contains(player.inventory)
    
    fun consume(inventory: Inventory): Boolean {
        items.forEach { 
            if (!it.consume(inventory)) return false
        }
        return true
    }
    
    fun consume(player: Player): Boolean =
        consume(player.inventory)
    
}
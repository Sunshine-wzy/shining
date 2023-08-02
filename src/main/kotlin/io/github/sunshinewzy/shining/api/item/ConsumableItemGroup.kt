package io.github.sunshinewzy.shining.api.item

import io.github.sunshinewzy.shining.api.item.universal.UniversalItem
import org.bukkit.inventory.ItemStack

class ConsumableItemGroup(
    var consume: Boolean,
    val items: MutableList<UniversalItem> = ArrayList()
) {
    
    constructor() : this(true)
    
    
    fun getItemStacks(): MutableList<ItemStack> = items.mapTo(ArrayList()) { it.getItemStack() }
    
}
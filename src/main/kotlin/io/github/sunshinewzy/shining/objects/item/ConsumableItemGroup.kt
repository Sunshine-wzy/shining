package io.github.sunshinewzy.shining.objects.item

import org.bukkit.inventory.ItemStack

class ConsumableItemGroup(
    var consume: Boolean,
    val items: MutableList<ItemStack> = ArrayList()
) {
    
    constructor() : this(true)
    
    
}
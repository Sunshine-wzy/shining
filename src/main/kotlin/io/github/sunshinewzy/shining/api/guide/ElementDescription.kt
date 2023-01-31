package io.github.sunshinewzy.shining.api.guide

import io.github.sunshinewzy.shining.utils.setNameAndLore
import org.bukkit.inventory.ItemStack

class ElementDescription(val name: String, val lore: List<String>) {
    
    constructor(name: String) : this(name, emptyList())
    
    constructor(name: String, vararg lore: String) : this(name, lore.toList())
    
    
    fun setOnItem(item: ItemStack): ItemStack =
        item.setNameAndLore(name, lore)
    
}
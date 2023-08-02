package io.github.sunshinewzy.shining.api.item.universal

import org.bukkit.inventory.ItemStack

class VanillaItem(private val item: ItemStack) : UniversalItem {

    override fun getItemStack(): ItemStack = item
    
}
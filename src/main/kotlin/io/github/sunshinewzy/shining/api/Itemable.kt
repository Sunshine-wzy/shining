package io.github.sunshinewzy.shining.api

import org.bukkit.inventory.ItemStack

interface Itemable {
    fun getItemStack(): ItemStack
}
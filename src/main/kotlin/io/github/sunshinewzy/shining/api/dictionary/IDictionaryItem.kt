package io.github.sunshinewzy.shining.api.dictionary

import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import org.bukkit.inventory.ItemStack

interface IDictionaryItem {
    
    fun getName(): NamespacedId
    
    fun getItemStack(): ItemStack
    
    fun getBehaviors(): List<ItemBehavior>
    
    fun hasName(): Boolean
    
}
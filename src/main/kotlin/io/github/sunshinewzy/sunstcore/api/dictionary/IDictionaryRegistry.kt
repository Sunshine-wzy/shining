package io.github.sunshinewzy.sunstcore.api.dictionary

import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.dictionary.DictionaryItem
import io.github.sunshinewzy.sunstcore.core.dictionary.item.behavior.ItemBehavior
import org.bukkit.inventory.ItemStack

interface IDictionaryRegistry {

    fun get(name: NamespacedId): DictionaryItem

    fun get(item: ItemStack): DictionaryItem

    fun getOrNull(name: NamespacedId): DictionaryItem?
    
    fun getOrNull(item: ItemStack): DictionaryItem?
    
    fun getById(id: String): List<DictionaryItem>
    
    
    fun registerItem(name: NamespacedId, item: ItemStack, vararg behaviors: ItemBehavior): DictionaryItem
    
    fun hasItem(name: NamespacedId): Boolean
    
}
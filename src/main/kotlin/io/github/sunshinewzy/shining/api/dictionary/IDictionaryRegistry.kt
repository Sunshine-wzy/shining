package io.github.sunshinewzy.shining.api.dictionary

import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryItem
import io.github.sunshinewzy.shining.core.dictionary.item.behavior.ItemBehavior
import org.bukkit.inventory.ItemStack

interface IDictionaryRegistry {

    fun get(name: NamespacedId): DictionaryItem?

    fun get(item: ItemStack): DictionaryItem?

    fun getOrFail(name: NamespacedId): DictionaryItem

    fun getOrFail(item: ItemStack): DictionaryItem
    
    fun getById(id: String): List<DictionaryItem>
    
    
    fun registerItem(name: NamespacedId, item: ItemStack, vararg behaviors: ItemBehavior): DictionaryItem
    
    fun hasItem(name: NamespacedId): Boolean
    
}
package io.github.sunshinewzy.shining.api.dictionary

import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import org.bukkit.inventory.ItemStack

interface IDictionaryRegistry {

    fun get(name: NamespacedId): IDictionaryItem?

    fun get(item: ItemStack): IDictionaryItem?

    fun getOrFail(name: NamespacedId): IDictionaryItem

    fun getOrFail(item: ItemStack): IDictionaryItem

    fun getById(id: String): List<IDictionaryItem>


    fun registerItem(name: NamespacedId, item: ItemStack, vararg behaviors: ItemBehavior): IDictionaryItem

    fun hasItem(name: NamespacedId): Boolean

}
package io.github.sunshinewzy.shining.core.dictionary

import io.github.sunshinewzy.shining.api.dictionary.IDictionaryItem
import io.github.sunshinewzy.shining.api.dictionary.IDictionaryRegistry
import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.utils.putListElement
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

object DictionaryRegistry : IDictionaryRegistry {

    private val itemsByName: MutableMap<NamespacedId, IDictionaryItem> = ConcurrentHashMap()
    private val itemsById: MutableMap<String, MutableList<IDictionaryItem>> = ConcurrentHashMap()


    override fun get(name: NamespacedId): IDictionaryItem? {
        return itemsByName[name]
    }

    override fun get(item: ItemStack): IDictionaryItem? {
        return item.dictionaryItem
    }

    override fun getOrFail(name: NamespacedId): IDictionaryItem {
        return get(name)!!
    }

    override fun getOrFail(item: ItemStack): IDictionaryItem {
        return get(item)!!
    }

    override fun getById(id: String): List<IDictionaryItem> {
        return itemsById[id.lowercase()] ?: emptyList()
    }

    override fun getItems(): Map<NamespacedId, IDictionaryItem> = itemsByName


    override fun registerItem(name: NamespacedId, item: ItemStack, vararg behaviors: ItemBehavior): IDictionaryItem {
        return register(DictionaryItem(name, item.setDictionaryName(name), *behaviors))
    }

    override fun hasItem(name: NamespacedId): Boolean {
        return itemsByName.containsKey(name)
    }


    private fun <T : IDictionaryItem> register(item: T): T {
        val name = item.getName()
        require(name !in itemsByName) { "Duplicate DictionaryItem name: $name" }

        itemsByName[name] = item
        itemsById.putListElement(name.id, item)

        return item
    }

}
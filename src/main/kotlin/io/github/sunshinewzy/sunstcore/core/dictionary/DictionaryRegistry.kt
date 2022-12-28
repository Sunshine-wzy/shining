package io.github.sunshinewzy.sunstcore.core.dictionary

import io.github.sunshinewzy.sunstcore.api.dictionary.IDictionaryRegistry
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.dictionary.DictionaryItem.Companion.dictionaryItem
import io.github.sunshinewzy.sunstcore.core.dictionary.DictionaryItem.Companion.setDictionaryName
import io.github.sunshinewzy.sunstcore.core.dictionary.item.behavior.ItemBehavior
import io.github.sunshinewzy.sunstcore.utils.putElement
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

object DictionaryRegistry : IDictionaryRegistry {
    
    private val itemsByName: MutableMap<NamespacedId, DictionaryItem> = ConcurrentHashMap()
    private val itemsById: MutableMap<String, MutableList<DictionaryItem>> = ConcurrentHashMap()


    override fun get(name: NamespacedId): DictionaryItem? {
        return itemsByName[name]
    }

    override fun get(item: ItemStack): DictionaryItem? {
        return item.dictionaryItem
    }

    override fun getOrFail(name: NamespacedId): DictionaryItem {
        return get(name)!!
    }

    override fun getOrFail(item: ItemStack): DictionaryItem {
        return get(item)!!
    }

    override fun getById(id: String): List<DictionaryItem> {
        return itemsById[id.lowercase()] ?: emptyList()
    }
    

    override fun registerItem(name: NamespacedId, item: ItemStack, vararg behaviors: ItemBehavior): DictionaryItem {
        return register(DictionaryItem(name, item.setDictionaryName(name), *behaviors))
    }

    override fun hasItem(name: NamespacedId): Boolean {
        return itemsByName.containsKey(name)
    }
    
    
    private fun <T: DictionaryItem> register(item: T): T {
        val name = item.name
        require(name !in itemsByName) { "Duplicate DictionaryItem name: $name" }
        
        itemsByName[name] = item
        itemsById.putElement(name.id, item)
        
        return item
    }
    
}
package io.github.sunshinewzy.shining.core.lang.item

import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.lang.LanguageNodePrefix
import io.github.sunshinewzy.shining.core.lang.getLanguageNode
import io.github.sunshinewzy.shining.core.lang.getLanguageNodeOrNull
import io.github.sunshinewzy.shining.core.lang.getLocale
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

open class NamespacedIdItem(item: ItemStack, val id: NamespacedId) : LocalizedItem(item, id.getLanguageNode(LanguageNodePrefix.ITEM.prefix)) {

    private val langItemCacheMap: MutableMap<String, LocalizedItem> by lazy { ConcurrentHashMap() }
    
    
    constructor(item: ItemStack, amount: Int, id: NamespacedId) : this(item, id) {
        this.amount = amount
    }
    constructor(type: Material, id: NamespacedId) : this(ItemStack(type), id)
    constructor(type: Material, amount: Int, id: NamespacedId) : this(ItemStack(type, amount), id)
    constructor(type: Material, damage: Short, amount: Int, id: NamespacedId) : this(ItemStack(type, amount, damage), id)
    
    
    fun toLangItem(sender: CommandSender): LocalizedItem {
        val locale = sender.getLocale()
        langItemCacheMap[locale]?.let { 
            return it
        }
        
        id.getLanguageNodeOrNull(LanguageNodePrefix.ITEM.prefix, locale)?.let { node -> 
            return LocalizedItem(clone(), node).also { langItemCacheMap[locale] = it }
        }
        return this
    }
    
}
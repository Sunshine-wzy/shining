package io.github.sunshinewzy.shining.core.lang.item

import io.github.sunshinewzy.shining.api.ShiningConfig
import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import io.github.sunshinewzy.shining.core.lang.LanguageFileLoader
import io.github.sunshinewzy.shining.core.lang.getLocale
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.utils.getMeta
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import java.util.concurrent.ConcurrentHashMap

open class LanguageItem(item: ItemStack, val localeToNode: (locale: String) -> LanguageNode) : LocalizedItem(item, localeToNode(ShiningConfig.language)) {

    private val langItemCacheMap: MutableMap<String, LocalizedItem> by lazy { ConcurrentHashMap() }
    private val stateItemCacheMap: MutableMap<String, LanguageItem> by lazy { ConcurrentHashMap() }
    
    private var isShiny = false
    
    
    constructor(item: ItemStack, amount: Int, node: (locale: String) -> LanguageNode) : this(item, node) {
        this.amount = amount
    }
    constructor(type: Material, node: (locale: String) -> LanguageNode) : this(ItemStack(type), node)
    constructor(type: Material, amount: Int, node: (locale: String) -> LanguageNode) : this(ItemStack(type, amount), node)
    constructor(type: Material, damage: Short, amount: Int, node: (locale: String) -> LanguageNode) : this(ItemStack(type, amount, damage), node)

    
    open fun shiny(): LanguageItem {
        if(isShiny) return this
        
        val meta = getMeta()
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        if(meta is EnchantmentStorageMeta) {
            meta.addStoredEnchant(Enchantment.LURE, 1, true)
        } else {
            meta.addEnchant(Enchantment.LURE, 1, true)
        }
        itemMeta = meta
        isShiny = true
        return this
    }

    fun toLocalizedItem(sender: CommandSender): LocalizedItem {
        val locale = sender.getLocale()
        if(locale == ShiningConfig.language)
            return this
        
        langItemCacheMap[locale]?.let {
            return it
        }
        
        return LocalizedItem(clone(), localeToNode(locale)).also { langItemCacheMap[locale] = it }
    }

    fun toStateItem(state: String): LanguageItem {
        if(state.isEmpty()) return this
        
        stateItemCacheMap[state]?.let {
            return it
        }
        
        return LanguageItem(clone()) { locale ->
            val node = localeToNode(locale)
            if(node is SectionNode) {
                node.section[state]?.let { stateNode ->
                    LanguageFileLoader.loadNode(stateNode)?.let {
                        return@LanguageItem it
                    }
                }
            }
            node
        }.also { stateItemCacheMap[state] = it }
    }
    
}
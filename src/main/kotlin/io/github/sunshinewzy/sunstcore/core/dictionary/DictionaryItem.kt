package io.github.sunshinewzy.sunstcore.core.dictionary

import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.objects.SBlock
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import java.util.concurrent.ConcurrentHashMap

open class DictionaryItem {
    val name: NamespacedId
    val item: ItemStack
    
    
    constructor(name: NamespacedId, item: ItemStack) {
        this.name = name
        this.item = item
    }
    
    constructor(item: ItemStack) {
        var theName = NamespacedId.NULL
        item.getDictionary { tag ->
            tag[NAME]?.asString()?.let { tagName ->
                NamespacedId.fromString(tagName)?.let {
                    theName = it
                }
            }
        }
        
        this.name = theName
        this.item = item
    }
    
    
    fun hasName(): Boolean = name != NamespacedId.NULL


    companion object {
        const val DICTIONARY = "dictionary"
        const val NAME = "name"
        
        private val blockItemMap = ConcurrentHashMap<NamespacedId, SBlock>()
        
        
        @SubscribeEvent(EventPriority.HIGHEST)
        fun onBlockPlace(event: BlockPlaceEvent) {
            
        }
        
        @SubscribeEvent(EventPriority.HIGHEST)
        fun onBlockBreak(event: BlockBreakEvent) {
            
        }
        
        
        fun ItemStack.dictionaryItem(): DictionaryItem? {
            return getDictionaryName()?.let { 
                DictionaryRegistry.getOrNull(it)
            }
        }
        
        
        private fun ItemStack.setDictionary(key: String, value: Any): ItemStack {
            val tag = getItemTag()
            val compound = tag[DICTIONARY]?.let { 
                if(it is ItemTag) it
                else null
            } ?: ItemTag()
            compound.put(key, value)
            tag[DICTIONARY] = compound
            return setItemTag(tag)
        }
        
        private fun ItemStack.getDictionary(key: String): ItemTagData? {
            getItemTag()[DICTIONARY]?.let { tag ->
                if(tag is ItemTag) {
                    return tag[key]
                }
            }
            
            return null
        }
        
        private fun ItemStack.getDictionary(action: (tag: ItemTag) -> Unit) {
            getItemTag()[DICTIONARY]?.let { tag ->
                if(tag is ItemTag) {
                    action(tag)
                }
            }
        }
        
        
        fun ItemStack.setDictionaryName(name: NamespacedId): ItemStack {
            return setDictionary(NAME, name.toString())
        }
        
        fun ItemStack.getDictionaryName(): NamespacedId? {
            return getDictionary(NAME)?.asString()?.let {
                NamespacedId.fromString(it)
            }
        }
        
    }
    
    
    enum class Type(val type: String) {
        
    }
    
}
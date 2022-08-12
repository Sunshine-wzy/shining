package io.github.sunshinewzy.sunstcore.core.dictionary

import io.github.sunshinewzy.sunstcore.api.NamespacedId
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

class DictionaryItem(val item: ItemStack) {
    var name: NamespacedId? = null
    var type: String = ""
    
    init {
        item.getDictionary { tag ->
            tag[NAME]?.asString()?.let { 
                name = NamespacedId.fromString(it)
            }
            
            tag[TYPE]?.asString()?.let { 
                type = it
            }
        }
    }


    companion object {
        const val DICTIONARY = "dictionary"
        const val NAME = "name"
        const val TYPE = "type"
        
        private val blockItemMap = ConcurrentHashMap<NamespacedId, SBlock>()
        
        
        @SubscribeEvent(EventPriority.HIGHEST)
        fun onBlockPlace(event: BlockPlaceEvent) {
            
        }
        
        @SubscribeEvent(EventPriority.HIGHEST)
        fun onBlockBreak(event: BlockBreakEvent) {
            
        }
        
        
        fun ItemStack.toDictionaryItem(): DictionaryItem =
            DictionaryItem(this)
        
        private fun ItemStack.setDictionary(key: String, value: Any) {
            val tag = getItemTag()
            val compound = tag[DICTIONARY]?.let { 
                if(it is ItemTag) it
                else null
            } ?: ItemTag()
            compound.put(key, value)
            tag[DICTIONARY] = compound
            setItemTag(tag)
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
        
        
        fun ItemStack.setDictionaryName(name: NamespacedId) {
            setDictionary(NAME, name.toString())
        }
        
        fun ItemStack.getDictionaryName(): NamespacedId? {
            getDictionary(NAME)?.asString()?.let { return NamespacedId.fromString(it) }
            
            return null
        }
        
    }
    
    
    enum class Type(val type: String) {
        
    }
    
}
package io.github.sunshinewzy.sunstcore.core.dictionary

import io.github.sunshinewzy.sunstcore.api.NamespacedKey
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

class DictionaryItem(val item: ItemStack) {
    val name: NamespacedKey? = item.getDictionaryName()


    companion object {
        const val DICTIONARY = "dictionary"
        const val NAME = "name"
        
        
        fun ItemStack.toDictionaryItem(): DictionaryItem =
            DictionaryItem(this)
        
        fun ItemStack.setDictionaryName(name: NamespacedKey) {
            val tag = getItemTag()
            val compound = tag[DICTIONARY]?.let { 
                if(it is ItemTag) it
                else null
            } ?: ItemTag()
            compound.put(NAME, name.toString())
            tag[DICTIONARY] = compound
            setItemTag(tag)
        }
        
        fun ItemStack.getDictionaryName(): NamespacedKey? {
            getItemTag()[DICTIONARY]?.let { tag ->
                if(tag is ItemTag) {
                    tag[NAME]?.asString()?.let { return NamespacedKey.fromString(it) }
                }
            }
            
            return null
        }
        
    }
    
}
package io.github.sunshinewzy.sunstcore.core.dictionary

import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.dictionary.item.behavior.ItemBehavior
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

open class DictionaryItem {
    val name: NamespacedId
    val item: ItemStack
    val behaviors: List<ItemBehavior>
    
    
    constructor(name: NamespacedId, item: ItemStack, behaviors: List<ItemBehavior>) {
        this.name = name
        this.item = item
        this.behaviors = behaviors
    }
    
    constructor(name: NamespacedId, item: ItemStack, vararg behaviors: ItemBehavior) : this(name, item, behaviors.toList())
    
    constructor(item: ItemStack, behaviors: List<ItemBehavior>) {
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
        this.behaviors = behaviors
    }
    
    constructor(item: ItemStack, vararg behaviors: ItemBehavior) : this(item, behaviors.toList())
    
    
    fun hasName(): Boolean = name != NamespacedId.NULL


    companion object {
        const val DICTIONARY = "dictionary"
        const val NAME = "name"
        
        
        val ItemStack.dictionaryItem: DictionaryItem?
            get() = getDictionaryName()?.let {
                DictionaryRegistry.getOrNull(it)
            }

        fun ItemStack.setDictionaryName(name: NamespacedId): ItemStack {
            return setDictionary(NAME, name.toString())
        }

        fun ItemStack.getDictionaryName(): NamespacedId? {
            return getDictionary(NAME)?.asString()?.let {
                NamespacedId.fromString(it)
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
        
    }
    
}
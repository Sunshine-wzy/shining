package io.github.sunshinewzy.shining.core.dictionary

import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.item.behavior.ItemBehavior
import io.github.sunshinewzy.shining.utils.getShiningNBT
import io.github.sunshinewzy.shining.utils.setShiningNBT
import org.bukkit.inventory.ItemStack

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
        this.name = item.getDictionaryName() ?: NamespacedId.NULL
        this.item = item
        this.behaviors = behaviors
    }

    constructor(item: ItemStack, vararg behaviors: ItemBehavior) : this(item, behaviors.toList())


    fun hasName(): Boolean = name != NamespacedId.NULL


    companion object {
        const val DICTIONARY = "dictionary"
    }
    
}


val ItemStack.dictionaryItem: DictionaryItem?
    get() = getDictionaryName()?.let {
        DictionaryRegistry.get(it)
    }

fun ItemStack.setDictionaryName(name: NamespacedId): ItemStack =
    setShiningNBT(DictionaryItem.DICTIONARY, name.toString())

fun ItemStack.getDictionaryName(): NamespacedId? =
    getShiningNBT()?.get(DictionaryItem.DICTIONARY)?.asString()?.let {
        NamespacedId.fromString(it)
    }

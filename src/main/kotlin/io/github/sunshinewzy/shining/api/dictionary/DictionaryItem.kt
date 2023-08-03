package io.github.sunshinewzy.shining.api.dictionary

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonValue
import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.item.universal.UniversalItem
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.utils.getShiningNBT
import io.github.sunshinewzy.shining.utils.setShiningNBT
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

@JsonTypeName("dictionary")
open class DictionaryItem : UniversalItem {
    @JsonValue
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

    
    override fun getItemStack(): ItemStack = item.clone()

    override fun contains(inventory: Inventory): Boolean =
        inventory.containsDictionaryItem(name, item.amount)

    override fun consume(inventory: Inventory): Boolean =
        inventory.removeDictionaryItem(name, item.amount)
    

    companion object {
        
        const val DICTIONARY = "dictionary"
        
        
        @JvmStatic
        @JsonCreator
        fun getByName(name: NamespacedId): DictionaryItem? =
            DictionaryRegistry.get(name)
        
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

fun Inventory.containsDictionaryItem(name: NamespacedId, amount: Int = 1): Boolean {
    if (amount <= 0) return true
    
    var cnt = amount
    storageContents.forEach { item ->
        if (item.isAir()) return@forEach
        
        item.getDictionaryName()?.let { 
            if (it == name) {
                cnt -= item.amount
                if (cnt <= 0) return true
            }
        }
    }
    
    return false
}

fun Inventory.containsDictionaryItem(dictionaryItem: DictionaryItem, amount: Int = 1): Boolean =
    containsDictionaryItem(dictionaryItem.name, amount)

fun Inventory.removeDictionaryItem(name: NamespacedId, amount: Int = 1): Boolean {
    if (amount <= 0) return true
    
    var cnt = amount
    storageContents.forEach { item ->
        if (item.isAir()) return@forEach
        
        item.getDictionaryName()?.let { 
            if (it == name) {
                val theCnt = cnt
                cnt -= item.amount
                
                if (item.amount > theCnt) item.amount -= theCnt
                else item.amount = 0
                
                if (cnt <= 0) return true
            }
        }
    }
    
    return false
}
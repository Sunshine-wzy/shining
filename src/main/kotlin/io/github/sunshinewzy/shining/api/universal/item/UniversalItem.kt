package io.github.sunshinewzy.shining.api.universal.item

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
interface UniversalItem : Cloneable {
    
    @JsonIgnore
    fun getItemStack(): ItemStack
    
    @JsonIgnore
    fun getItemAmount(): Int
    
    fun contains(inventory: Inventory, checkMeta: Boolean, checkName: Boolean, checkLore: Boolean): Boolean
    
    fun contains(inventory: Inventory): Boolean =
        contains(inventory, checkMeta = true, checkName = true, checkLore = true)
    
    fun contains(inventory: Inventory, amount: Int, checkMeta: Boolean, checkName: Boolean, checkLore: Boolean): Boolean
    
    fun contain(inventory: Inventory, amount: Int): Boolean =
        contains(inventory, amount, checkMeta = true, checkName = true, checkLore = true)
    
    fun consume(inventory: Inventory, checkMeta: Boolean, checkName: Boolean, checkLore: Boolean): Boolean
    
    fun consume(inventory: Inventory): Boolean =
        consume(inventory, checkMeta = true, checkName = true, checkLore = true)
    
    fun openEditor(player: Player, context: GuideContext)
    
    fun isSimilar(other: UniversalItem, checkAmount: Boolean, checkMeta: Boolean, checkName: Boolean, checkLore: Boolean): Boolean

    fun isSimilar(other: UniversalItem): Boolean =
        isSimilar(other, checkAmount = true, checkMeta = true, checkName = true, checkLore = true)
    
    fun isSimilar(other: ItemStack, checkAmount: Boolean, checkMeta: Boolean, checkName: Boolean, checkLore: Boolean): Boolean
    
    fun isSimilar(other: ItemStack) =
        isSimilar(other, checkAmount = true, checkMeta = true, checkName = true, checkLore = true)
    
    public override fun clone(): UniversalItem
    
}
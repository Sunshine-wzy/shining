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
    
    fun contains(inventory: Inventory): Boolean
    
    fun contains(inventory: Inventory, amount: Int): Boolean
    
    fun consume(inventory: Inventory): Boolean
    
    fun openEditor(player: Player, context: GuideContext)
    
    fun isSimilar(other: UniversalItem, checkAmount: Boolean): Boolean

    public override fun clone(): UniversalItem
    
}
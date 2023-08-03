package io.github.sunshinewzy.shining.api.item.universal

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.guide.GuideContext
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
interface UniversalItem {
    
    @JsonIgnore
    fun getItemStack(): ItemStack
    
    fun contains(inventory: Inventory): Boolean
    
    fun consume(inventory: Inventory): Boolean
    
    fun openEditor(player: Player, context: GuideContext)
    
}
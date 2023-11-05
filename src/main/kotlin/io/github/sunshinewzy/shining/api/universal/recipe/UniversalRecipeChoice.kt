package io.github.sunshinewzy.shining.api.universal.recipe

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.bukkit.inventory.ItemStack

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
interface UniversalRecipeChoice : Cloneable {
    
    fun getItemStack(): ItemStack
    
    fun test(item: ItemStack): Boolean

    fun iterator(): Iterator<ItemStack>

    public override fun clone(): UniversalRecipeChoice
    
}
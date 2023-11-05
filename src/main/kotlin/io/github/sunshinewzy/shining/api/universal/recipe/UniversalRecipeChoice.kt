package io.github.sunshinewzy.shining.api.universal.recipe

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.bukkit.inventory.ItemStack

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
interface UniversalRecipeChoice : Cloneable {
    
    fun test(item: ItemStack): Boolean

    public override fun clone(): UniversalRecipeChoice
    
}
package io.github.sunshinewzy.shining.core.universal.recipe

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.api.universal.item.UniversalItem
import io.github.sunshinewzy.shining.api.universal.recipe.UniversalRecipeChoice
import io.github.sunshinewzy.shining.core.universal.item.VanillaUniversalItem
import org.bukkit.inventory.ItemStack

@JsonTypeName("item")
data class ItemUniversalRecipeChoice(val choices: List<UniversalItem>) : UniversalRecipeChoice {
    
    init {
        check(choices.isNotEmpty()) { "Must have at least one choice." }
    }
    
    constructor(item: UniversalItem) : this(listOf(item))
    
    constructor(vararg items: UniversalItem) : this(items.toList())
    
    constructor() : this(VanillaUniversalItem())

    
    override fun getItemStack(): ItemStack =
        choices.first().getItemStack()

    override fun test(item: ItemStack): Boolean {
        choices.forEach { 
            if (it.isSimilar(item))
                return true
        }
        return false
    }

    override fun iterator(): Iterator<ItemStack> =
        choices.map { it.getItemStack() }.iterator()

    override fun clone(): ItemUniversalRecipeChoice = ItemUniversalRecipeChoice(ArrayList(choices))
    
}
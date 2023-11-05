package io.github.sunshinewzy.shining.core.universal.recipe

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.api.universal.recipe.UniversalRecipeChoice
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@JsonTypeName("material")
data class MaterialUniversalRecipeChoice(val choices: List<Material>) : UniversalRecipeChoice {

    init {
        check(choices.isNotEmpty()) { "Must have at least one choice." }
    }
    
    constructor(type: Material) : this(listOf(type))
    
    constructor(vararg types: Material) : this(types.toList())
    
    constructor() : this(Material.STONE)


    override fun getItemStack(): ItemStack =
        ItemStack(choices.first())

    override fun test(item: ItemStack): Boolean {
        choices.forEach { 
            if (item.type == it)
                return true
        }
        return false
    }

    override fun iterator(): Iterator<ItemStack> =
        choices.map { ItemStack(it) }.iterator()

    override fun clone(): MaterialUniversalRecipeChoice = MaterialUniversalRecipeChoice(ArrayList(choices))
    
}
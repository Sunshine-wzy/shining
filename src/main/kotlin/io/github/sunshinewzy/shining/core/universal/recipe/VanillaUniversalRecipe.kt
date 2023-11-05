package io.github.sunshinewzy.shining.core.universal.recipe

import io.github.sunshinewzy.shining.api.universal.item.UniversalItem
import io.github.sunshinewzy.shining.api.universal.recipe.UniversalRecipeChoice
import io.github.sunshinewzy.shining.api.universal.recipe.UniversalSingleRecipe
import io.github.sunshinewzy.shining.core.universal.item.VanillaUniversalItem
import io.github.sunshinewzy.shining.utils.getRecipeChoice
import io.github.sunshinewzy.shining.utils.toUniversalRecipeChoice
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

class VanillaUniversalRecipe : UniversalSingleRecipe {
    
    private val recipe: List<UniversalRecipeChoice>
    private val result: UniversalItem
    
    constructor(recipe: List<UniversalRecipeChoice>, result: UniversalItem) {
        this.recipe = recipe
        this.result = result
    }
    
    constructor(recipe: Recipe) {
        this.recipe = when (recipe) {
            is ShapedRecipe -> {
                recipe.getRecipeChoice().map { it.toUniversalRecipeChoice() }
            }
            is ShapelessRecipe -> {
                recipe.choiceList.map { it.toUniversalRecipeChoice() }
            }
            else -> throw IllegalArgumentException("Unsupported recipe type: ${recipe.javaClass}")
        }
        
        this.result = VanillaUniversalItem(recipe.result)
    }
    

    override fun getRecipe(): List<UniversalRecipeChoice> = recipe

    override fun getResult(): UniversalItem = result
    
}
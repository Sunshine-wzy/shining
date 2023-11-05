package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.api.universal.recipe.UniversalRecipeChoice
import io.github.sunshinewzy.shining.core.universal.item.VanillaUniversalItem
import io.github.sunshinewzy.shining.core.universal.recipe.ItemUniversalRecipeChoice
import io.github.sunshinewzy.shining.core.universal.recipe.MaterialUniversalRecipeChoice
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe

fun ShapedRecipe.getRecipeItem(): Array<ItemStack> {
    val recipe = Array(9) { ItemStack(Material.AIR) }
    val rows = shape
    val ingredients = ingredientMap

    for (i in rows.indices) {
        val base = i * 3
        val str = rows[i]

        for (j in str.indices) {
            ingredients[str[j]]?.let {
                recipe[base + j] = it.clone()
            }
        }
    }

    return recipe
}

fun ShapedRecipe.getRecipeChoice(): Array<RecipeChoice> {
    val recipe: Array<RecipeChoice> = Array(9) { RecipeChoice.MaterialChoice(Material.AIR) }
    val rows = shape
    val ingredients = choiceMap

    for (i in rows.indices) {
        val base = i * 3
        val str = rows[i]

        for (j in str.indices) {
            ingredients[str[j]]?.let {
                recipe[base + j] = it
            }
        }
    }

    return recipe
}

fun RecipeChoice.toUniversalRecipeChoice(): UniversalRecipeChoice {
    if (this is RecipeChoice.ExactChoice) {
        return ItemUniversalRecipeChoice(choices.map { VanillaUniversalItem(it) })
    }
    if (this is RecipeChoice.MaterialChoice) {
        return MaterialUniversalRecipeChoice(ArrayList(choices))
    }
    throw UnsupportedOperationException("Unsupported RecipeChoice type: $javaClass")
}
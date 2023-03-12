package io.github.sunshinewzy.shining.objects

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin

class SShapedRecipe(key: NamespacedKey, result: ItemStack) : ShapedRecipe(key, result) {
    constructor(plugin: JavaPlugin, key: String, result: ItemStack) : this(NamespacedKey(plugin, key), result)

    constructor(
        key: NamespacedKey,
        result: ItemStack,
        ingredient: Map<Char, Material>,
        line1: String = "",
        line2: String = "",
        line3: String = ""
    ) : this(key, result) {
        shape(line1, line2, line3)
        ingredient.forEach { (char, value) ->
            setIngredient(char, value)
        }
    }

    constructor(
        plugin: JavaPlugin,
        key: String,
        result: ItemStack,
        ingredient: Map<Char, Material>,
        line1: String = "",
        line2: String = "",
        line3: String = ""
    ) : this(NamespacedKey(plugin, key), result, ingredient, line1, line2, line3)


    companion object {
        fun byChoice(
            key: NamespacedKey,
            result: ItemStack,
            ingredient: Map<Char, SRecipeChoice>,
            line1: String = "",
            line2: String = "",
            line3: String = ""
        ): SShapedRecipe =
            SShapedRecipe(key, result).apply {
                shape(line1, line2, line3)
                ingredient.forEach { (char, choice) ->
                    setIngredient(char, choice.getChoice())
                }
            }

        fun byChoice(
            plugin: JavaPlugin,
            key: String,
            result: ItemStack,
            ingredient: Map<Char, SRecipeChoice>,
            line1: String = "",
            line2: String = "",
            line3: String = ""
        ): SShapedRecipe = byChoice(NamespacedKey(plugin, key), result, ingredient, line1, line2, line3)

    }
}
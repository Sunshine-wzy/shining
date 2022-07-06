package io.github.sunshinewzy.sunstcore.objects

import io.github.sunshinewzy.sunstcore.interfaces.Materialsable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice

class SRecipeChoice {
    val materials = ArrayList<Material>()
    val items = ArrayList<ItemStack>()
    
    
    constructor(vararg choices: Material) {
        materials += choices
    }
    
    constructor(vararg choices: ItemStack) {
        items += choices
    }
    
    constructor(choices: List<Material>) {
        materials += choices
    }
    
    constructor(choices: Materialsable) : this(choices.types())
    
    
    
    fun toMaterialChoice(): RecipeChoice.MaterialChoice {
        val list = ArrayList<Material>()
        list += materials
        items.forEach { 
            list += it.type
        }
        return RecipeChoice.MaterialChoice(list)
    }
    
    fun toExactChoice(): RecipeChoice.ExactChoice {
        val list = ArrayList<ItemStack>()
        list += items
        materials.forEach { 
            list += SItem(it)
        }
        return RecipeChoice.ExactChoice(list)
    }
    
    fun getChoice(): RecipeChoice {
        if(items.isNotEmpty()) {
            return toExactChoice()
        }
        
        return toMaterialChoice()
    }
    
}
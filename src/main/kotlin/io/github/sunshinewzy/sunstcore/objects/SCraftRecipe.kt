package io.github.sunshinewzy.sunstcore.objects

import io.github.sunshinewzy.sunstcore.exceptions.IllegalRecipeException
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.isItemSimilar
import io.github.sunshinewzy.sunstcore.utils.getSquareItems
import io.github.sunshinewzy.sunstcore.utils.putElement
import io.github.sunshinewzy.sunstcore.utils.typeHash
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

sealed class SCraftRecipe(
    val size: Int,
    val output: ItemStack,
    val input: Array<ItemStack>
) {
    
    class Size3(output: ItemStack, input: Array<ItemStack>) : SCraftRecipe(3, output, input) {
        constructor(output: ItemStack, shape: String, ingredients: Map<Char, ItemStack>) : this(output, Array(9) { ItemStack(Material.AIR) }) {
            shapeRecipe(size, shape, ingredients)
        }
        
        constructor(output: ItemStack, line1: String, line2: String, line3: String, ingredients: Map<Char, ItemStack>) :this(output, "$line1\n$line2\n$line3", ingredients)
        
    }
    
    class Size5(output: ItemStack, input: Array<ItemStack>) : SCraftRecipe(5, output, input) {
        constructor(output: ItemStack, shape: String, ingredients: Map<Char, ItemStack>) : this(output, Array(25) { ItemStack(Material.AIR) }) {
            shapeRecipe(size, shape, ingredients)
        }

        constructor(output: ItemStack, line1: String, line2: String, line3: String, line4: String, line5: String, ingredients: Map<Char, ItemStack>) :this(output, "$line1\n$line2\n$line3\n$line4\n$line5", ingredients)
    }
    
    
    fun shapeRecipe(size: Int, shape: String, ingredients: Map<Char, ItemStack>) {
        val lines = shape.split("\n")
        lines.forEachIndexed forEachY@{ y, line ->
            if(y >= size) throw IllegalRecipeException(shape, "The lines of crafting shape should be less than or equal to $size, not ${lines.size}.")
            
            line.forEachIndexed forEachX@{ x, char ->
                if(char == ' ') return@forEachX
                if(x >= size) throw IllegalRecipeException(shape, "The characters of crafting rows should be less than or equal to $size, not ${line.length}.")
                
                input[y * size + x] = ingredients[char] ?: throw IllegalRecipeException(shape, "The character '$char' should be in the ingredients.")
            }
        }
    }
    
    fun match(items: Array<ItemStack>, checkLore: Boolean = true, checkAmount: Boolean = true, checkDurability: Boolean = false): Boolean {
        if(items.size != input.size) return false
        
        for(i in input.indices) {
            if(!items[i].isItemSimilar(input[i], checkLore, checkAmount, checkDurability))
                return false
        }
        
        return true
    }
    
    fun match(items: Array<ItemStack>): Boolean = match(items, true)

    fun match(inventory: Inventory, x: Int, y: Int, checkLore: Boolean = true, checkAmount: Boolean = true, checkDurability: Boolean = false): Boolean =
        match(inventory.getSquareItems(x, y, size), checkLore, checkAmount, checkDurability)
    
    fun match(inventory: Inventory, x: Int, y: Int): Boolean =
        match(inventory, x, y, true)
    
    fun match(inventory: Inventory, slot: Int, checkLore: Boolean = true, checkAmount: Boolean = true, checkDurability: Boolean = false): Boolean {
        val coord = slot.toCoordinate()
        return match(inventory, coord.x, coord.y, checkLore, checkAmount, checkDurability)
    }
    
    fun match(inventory: Inventory, slot: Int): Boolean =
        match(inventory, slot, true)
    
    
    companion object Manager {
        private val recipes = HashMap<Int, ArrayList<SCraftRecipe>>()
        
        
        @JvmStatic
        fun addRecipe(recipe: SCraftRecipe) {
            recipes.putElement(recipe.input.typeHash(), recipe)
        }
        
        @JvmStatic
        fun matchRecipe(items: Array<ItemStack>, checkLore: Boolean = true, checkAmount: Boolean = true, checkDurability: Boolean = false): SCraftRecipe? {
            recipes[items.typeHash()]?.forEach { recipe ->
                if(recipe.match(items, checkLore, checkAmount, checkDurability))
                    return recipe
            }
            return null
        }
        
        @JvmStatic
        fun matchRecipe(items: Array<ItemStack>): SCraftRecipe? = matchRecipe(items, true)

        @JvmStatic
        fun matchRecipe(inventory: Inventory, x: Int, y: Int, size: Int, checkLore: Boolean = true, checkAmount: Boolean = true, checkDurability: Boolean = false): SCraftRecipe? =
            matchRecipe(inventory.getSquareItems(x, y, size), checkLore, checkAmount, checkDurability)

        @JvmStatic
        fun matchRecipe(inventory: Inventory, x: Int, y: Int, size: Int): SCraftRecipe? =
            matchRecipe(inventory, x, y, size, true)

        @JvmStatic
        fun matchRecipe(inventory: Inventory, slot: Int, size: Int, checkLore: Boolean = true, checkAmount: Boolean = true, checkDurability: Boolean = false): SCraftRecipe? {
            val coord = slot.toCoordinate()
            return matchRecipe(inventory, coord.x, coord.y, size, checkLore, checkAmount, checkDurability)
        }

        @JvmStatic
        fun matchRecipe(inventory: Inventory, slot: Int, size: Int): SCraftRecipe? =
            matchRecipe(inventory, slot, size, true)
    }
    
}
package io.github.sunshinewzy.shining.api.universal.recipe

import io.github.sunshinewzy.shining.api.universal.item.UniversalItem

interface UniversalRecipe {
    
    fun getRecipe(): List<UniversalRecipeChoice>
    
    fun getResult(): UniversalItem
    
    fun getResults(): List<UniversalItem>

    fun iterator(): UniversalRecipeIterator =
        UniversalRecipeIterator(this)
    
    fun hasChoice(): Boolean {
        val iterator = iterator()
        iterator.next()
        return iterator.hasNext()
    }
    
}
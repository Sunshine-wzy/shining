package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.MachineProperty
import io.github.sunshinewzy.shining.core.dictionary.DictionaryItem
import io.github.sunshinewzy.shining.core.machine.recipe.MachineRecipe

/**
 * Represent a single block machine.
 */
open class SimpleMachine(
    property: MachineProperty,
    val item: DictionaryItem
) {
    
    private val recipes: MutableList<MachineRecipe> = arrayListOf()

}
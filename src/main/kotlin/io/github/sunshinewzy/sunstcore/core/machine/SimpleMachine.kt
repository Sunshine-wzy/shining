package io.github.sunshinewzy.sunstcore.core.machine

import io.github.sunshinewzy.sunstcore.core.dictionary.DictionaryItem
import io.github.sunshinewzy.sunstcore.core.machine.recipe.MachineRecipe

/**
 * Represent a single block machine.
 */
open class SimpleMachine(
    property: MachineProperty,
    val item: DictionaryItem
) : AbstractMachine(property) {
    private val recipes: MutableList<MachineRecipe> = arrayListOf()
    
    
    override fun run() {
        TODO("Not yet implemented")
    }

    override fun edit() {
        TODO("Not yet implemented")
    }
    
}
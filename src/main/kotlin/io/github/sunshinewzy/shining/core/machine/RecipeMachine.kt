package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.core.machine.recipe.MachineRecipe

abstract class RecipeMachine(property: MachineProperty) : AbstractMachine(property) {

    private val recipes: MutableList<MachineRecipe> = arrayListOf()


    fun addRecipe(recipe: MachineRecipe) {
        recipes += recipe
    }

}
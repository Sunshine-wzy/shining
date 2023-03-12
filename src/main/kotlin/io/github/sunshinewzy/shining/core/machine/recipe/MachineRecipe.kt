package io.github.sunshinewzy.shining.core.machine.recipe

import io.github.sunshinewzy.shining.core.machine.behavior.MachineBehavior
import io.github.sunshinewzy.shining.core.machine.block.io.MachineInput
import io.github.sunshinewzy.shining.core.machine.block.io.MachineOutput

class MachineRecipe {
    private val inputs: MutableList<MachineInput> = arrayListOf()
    private val outputs: MutableList<MachineOutput> = arrayListOf()
    private val behaviors: MutableList<MachineBehavior> = arrayListOf()


    fun addInput(input: MachineInput) {
        inputs += input
    }

    fun addOutput(output: MachineOutput) {
        outputs += output
    }

    fun addBehavior(behavior: MachineBehavior) {
        behaviors += behavior
    }

}
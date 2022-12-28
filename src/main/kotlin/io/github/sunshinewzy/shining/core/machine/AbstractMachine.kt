package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachine

abstract class AbstractMachine(override val property: MachineProperty) : IMachine {

    open fun register() {
//        Shining.machineManager.register(this)
    }
    

    override fun onTick() {}

    override fun onAsyncTick() {}
    
}
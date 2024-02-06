package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachineWrench
import io.github.sunshinewzy.shining.api.machine.MachineProperty
import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure

open class Machine : AbstractMachine {
    
    constructor(property: MachineProperty, structure: IMachineStructure) : super(property, structure)
    constructor() : super()
    

    override fun register(wrench: IMachineWrench): Machine {
        super.register(wrench)
        return this
    }

    override fun register(): Machine {
        super.register()
        return this
    }
    
}
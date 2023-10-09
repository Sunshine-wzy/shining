package io.github.sunshinewzy.shining.core.machine.structure

import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure

abstract class AbstractMachineStructure : IMachineStructure {

    override var strictMode: Boolean = true
    
    override var ignoreAir: Boolean = true
    
}
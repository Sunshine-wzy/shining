package io.github.sunshinewzy.sunstcore.api.machine

import io.github.sunshinewzy.sunstcore.core.machine.MachineProperty

/**
 * Machine represents a block or a collection of blocks which can be interacted and has its own state.
 */
interface IMachine {
    
    val property: MachineProperty
    
    
    fun run()
    
    fun edit()
    
}
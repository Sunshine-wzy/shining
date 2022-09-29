package io.github.sunshinewzy.sunstcore.api.machine

import io.github.sunshinewzy.sunstcore.core.machine.MachineProperty
import io.github.sunshinewzy.sunstcore.interfaces.tick.Tickable

/**
 * Machine represents a block or a collection of blocks which can be interacted and has its own state.
 */
interface IMachine : Tickable {
    
    val property: MachineProperty
    
    
    fun run()
    
    fun edit()
    
}
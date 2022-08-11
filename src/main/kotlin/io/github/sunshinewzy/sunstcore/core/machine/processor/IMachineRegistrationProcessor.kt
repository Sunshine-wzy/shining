package io.github.sunshinewzy.sunstcore.core.machine.processor

import io.github.sunshinewzy.sunstcore.core.machine.IMachine

/**
 * Manage how to activate machines.
 * 
 * For example, register a listener and check the explicit type of the machine to determine whether the machine needs to be activated.
 */
interface IMachineRegistrationProcessor {

    /**
     * When a machine is being registered, it will be executed.
     */
    fun onRegister(machine: IMachine)
    
}
package io.github.sunshinewzy.sunstcore.api.machine

import io.github.sunshinewzy.sunstcore.objects.SLocation

/**
 * Manage all lifecycles of machines.
 */
interface IMachineManager {

    /**
     * Register a [machine].
     * 
     * It means SunSTCore starts to judge whether the [machine] is created.
     * 
     * Invoke [IMachineRegistrationProcessor.onRegister]
     */
    fun register(machine: IMachine)

    /**
     * Unregister a [machine].
     */
    fun unregister(machine: IMachine)

    /**
     * Check whether the [machine] has already been registered. 
     */
    fun isRegistered(machine: IMachine): Boolean

    /**
     * When a machine is created, it does not mean a new instance of [machine] will be created.
     * However, it will execute [activate], which will bind the [location] to the [machine].
     */
    fun activate(location: SLocation, machine: IMachine)

    /**
     * When a machine is destroyed, [deactivate] will be executed, which will unbind the [location] to the machine.
     */
    fun deactivate(location: SLocation)

    /**
     * Run the machine at [location].
     */
    fun run(location: SLocation)

    /**
     * Register a [processor].
     * 
     * The [processor] decides how to activate machines.
     */
    fun registerProcessor(processor: IMachineRegistrationProcessor)

    /**
     * Unregister a [processor].
     */
    fun unregisterProcessor(processor: IMachineRegistrationProcessor)

    /**
     * Check whether the [processor] has already been registered.
     */
    fun isProcessorRegistered(processor: IMachineRegistrationProcessor): Boolean
    
}
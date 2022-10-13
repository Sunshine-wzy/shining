package io.github.sunshinewzy.sunstcore.api.machine

import io.github.sunshinewzy.sunstcore.objects.SPosition

/**
 * Manage all lifecycles of machines.
 */
interface IMachineManager {

    /**
     * When a machine is created, it does not mean a new instance of [machine] will be created.
     * However, it will execute [activate], which will bind the [location] to the [machine].
     */
    fun activate(location: SPosition, machine: IMachine)

    /**
     * When a machine is destroyed, [deactivate] will be executed, which will unbind the [location] to the machine.
     */
    fun deactivate(location: SPosition)

    /**
     * Run the machine at [location].
     */
    fun run(location: SPosition)

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
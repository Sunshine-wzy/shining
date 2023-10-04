package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.objects.position.Position3D

/**
 * Manage all lifecycles of machines.
 */
interface IMachineManager {

    /**
     * When a machine is created, it does not mean a new instance of [machine] will be created.
     * However, it will execute [activate], which will bind the [location] to the [machine].
     */
    fun activate(location: Position3D, machine: IMachine)

    /**
     * When a machine is destroyed, [deactivate] will be executed, which will unbind the [location] to the machine.
     */
    fun deactivate(location: Position3D)

    /**
     * Run the machine at [location].
     */
    fun run(location: Position3D)

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
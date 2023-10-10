package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.objects.position.Position3D

/**
 * Manage all lifecycles of machines.
 */
interface IMachineManager {

    /**
     * When a machine is created, it does not mean a new instance of [machine] will be created.
     * However, it will execute [activate], which will bind the [position] to the [machine].
     */
    fun activate(position: Position3D, machine: IMachine)

    /**
     * When a machine is destroyed, [deactivate] will be executed, which will unbind the [position] to the machine.
     */
    fun deactivate(position: Position3D)

    /**
     * Run the machine at [position].
     */
    fun run(position: Position3D)

    /**
     * Check if there is a machine at [position].
     */
    fun hasMachine(position: Position3D): Boolean

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
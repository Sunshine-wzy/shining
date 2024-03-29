package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.machine.event.run.MachineRunEvent
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D
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
    fun deactivate(position: Position3D): IMachine?

    /**
     * Run the machine at [position].
     */
    fun run(position: Position3D): MachineRunEvent?

    /**
     * Check if there is a machine at [position].
     */
    fun hasMachine(position: Position3D): Boolean

    /**
     * Get the machine at [position].
     * 
     * If there is no machine at [position], it will return null.
     */
    fun getMachine(position: Position3D): IMachine?

    fun bindCoordinateEventPosition(machine: IMachine)
    
    fun bindCoordinateEventPosition(machine: IMachine, coordinate: Coordinate3D)
    
    fun unbindCoordinateEventPosition(machine: IMachine)
    
    fun unbindCoordinateEventPosition(machine: IMachine, coordinate: Coordinate3D)

    /**
     * Get the center of the interactive block.
     */
    fun getInteractiveBlockCenter(position: Position3D): Position3D?

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
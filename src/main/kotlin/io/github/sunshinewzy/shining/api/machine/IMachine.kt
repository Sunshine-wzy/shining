package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.machine.component.IMachineComponent
import io.github.sunshinewzy.shining.api.machine.component.MachineComponentLifeCycle
import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure

/**
 * Machine represents a block or a collection of blocks which can be interacted and has its own state.
 */
interface IMachine {

    val property: MachineProperty
    
    var structure: IMachineStructure


    /**
     * Register this [IMachine] which can be activated by [wrench].
     */
    fun register(wrench: IMachineWrench): IMachine

    /**
     * Register this [IMachine] which cannot be activated automatically.
     * You need to call [IMachineManager.activate] manually.
     */
    fun register(): IMachine
    
    /**
     * @return A Component of the matching type, otherwise throw an exception.
     */
    fun <T : IMachineComponent> getComponent(type: Class<T>): T

    /**
     * @return A Component of the matching type, otherwise null if no Component is found.
     */
    fun <T : IMachineComponent> getComponentOrNull(type: Class<T>): T?

    /**
     * Add the component to the machine.
     * 
     * @param component An instance of [type].
     * @return [component]
     */
    fun <T : IMachineComponent> addComponent(type: Class<T>, component: T): T

    /**
     * Add the component to the machine. The [type] will be instantiated by reflection.
     * 
     * @param type The class must have a constructor with one parameter [IMachine]
     * @return An instance of [type]
     */
    fun <T : IMachineComponent> addComponent(type: Class<T>): T

    /**
     * Remove the component from the machine.
     */
    fun <T : IMachineComponent> removeComponent(type: Class<T>): T?

    /**
     * Check if the machine has the component of [type].
     */
    fun <T : IMachineComponent> hasComponent(type: Class<T>): Boolean

    /**
     * Check if [type] has the [lifeCycle].
     */
    fun <T : IMachineComponent> hasComponentLifeCycle(type: Class<T>, lifeCycle: MachineComponentLifeCycle): Boolean

}
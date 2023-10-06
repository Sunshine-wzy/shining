package io.github.sunshinewzy.shining.api.machine.component

import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineContext

interface IMachineComponent {

    /**
     * The machine this component is attached to. A component is always attached to a machine.
     */
    val machine: IMachine

    /**
     * @return A Component of the matching type, otherwise throw an exception.
     */
    fun <T : IMachineComponent> getComponent(type: Class<T>): T =
        machine.getComponent(type)

    /**
     * @return A Component of the matching type, otherwise null if no Component is found.
     */
    fun <T : IMachineComponent> getComponentOrNull(type: Class<T>): T? =
        machine.getComponentOrNull(type)

    /**
     * Add the component to the machine.
     *
     * @param component An instance of [type].
     * @return [component]
     */
    fun <T : IMachineComponent> addComponent(type: Class<T>, component: T): T =
        machine.addComponent(type, component)

    /**
     * Add the component to the machine. The [type] will be instantiated by reflection.
     *
     * @param type The class must have a constructor with one parameter [IMachine].
     * @return An instance of [type]
     */
    fun <T : IMachineComponent> addComponent(type: Class<T>): T =
        machine.addComponent(type)

    /**
     * Remove the component from the machine.
     */
    fun <T : IMachineComponent> removeComponent(type: Class<T>): T? =
        machine.removeComponent(type)

    /**
     * Executed when the component is added to the machine.
     */
    fun onLoad() {}

    /**
     * Executed when the component is enabled.
     */
    fun onEnable() {}
    
    /**
     * Executed when the machine is created.
     */
    fun onActive(context: IMachineContext) {}

    /**
     * Executed every tick.
     */
    fun onUpdate(context: IMachineContext) {}

    /**
     * Executed when the machine is destroyed.
     */
    fun onDeactive(context: IMachineContext) {}

    /**
     * Executed when the component is disabled.
     */
    fun onDisable() {}
    
    /**
     * Executed when the component is removed from the machine.
     */
    fun onDestroy() {}
    
}
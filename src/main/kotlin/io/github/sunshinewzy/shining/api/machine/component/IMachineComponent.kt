package io.github.sunshinewzy.shining.api.machine.component

import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineContext
import io.github.sunshinewzy.shining.api.machine.IMachineInteractContext
import io.github.sunshinewzy.shining.api.machine.IMachineRunContext

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
     * Check if the machine has the component of [type].
     */
    fun <T : IMachineComponent> hasComponent(type: Class<T>): Boolean =
        machine.hasComponent(type)

    /**
     * Check if [type] has the [lifecycle].
     */
    fun <T : IMachineComponent> hasComponentLifecycle(type: Class<T>, lifecycle: MachineComponentLifecycle): Boolean =
        machine.hasComponentLifecycle(type, lifecycle)


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
    fun onActivate(context: IMachineContext) {}

    /**
     * Executed every tick.
     */
    fun onUpdate(context: IMachineContext) {}

    /**
     * Executed when an interactive block of the machine is interacted with by a player.
     */
    fun onInteract(context: IMachineInteractContext) {}

    /**
     * Executed when the machine is triggered to run.
     * 
     * The player interacts with an interactive block of the machine by right click can trigger it to run,
     * which means [onInteract] will be executed first and then [onRun] will be executed.
     * 
     * Additionally, the machine can be triggered to run manually.
     */
    fun onRun(context: IMachineRunContext) {}

    /**
     * Executed when the machine is destroyed.
     */
    fun onDeactivate(context: IMachineContext) {}

    /**
     * Executed when the component is disabled.
     */
    fun onDisable() {}
    
    /**
     * Executed when the component is removed from the machine.
     */
    fun onDestroy() {}
    
}
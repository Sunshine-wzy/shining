package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.machine.component.IMachineComponent
import io.github.sunshinewzy.shining.api.machine.component.MachineComponentLifecycle
import io.github.sunshinewzy.shining.api.machine.event.*
import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D
import java.util.function.BiConsumer

/**
 * Machine represents a block or a collection of blocks which can be interacted and has its own state.
 */
interface IMachine {

    val property: MachineProperty
    var structure: IMachineStructure
    val blueprint: IBlueprintClass


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
     * Registers all the events in the given listener class
     *
     * @param listener Listener to register
     */
    fun registerEvents(listener: MachineListener)

    /**
     * Registers the specified executor to the given event class
     *
     * @param event Event type to register
     * @param listener Listener to register
     * @param priority Priority to register this event at
     * @param executor EventExecutor to register
     */
    fun registerEvent(
        event: Class<out MachineEvent>,
        listener: MachineListener,
        priority: MachineEventPriority,
        executor: MachineEventExecutor
    ) { registerEvent(event, listener, priority, executor, true) }

    /**
     * Registers the specified executor to the given event class
     *
     * @param event Event type to register
     * @param listener Listener to register
     * @param priority Priority to register this event at
     * @param executor EventExecutor to register
     * @param ignoreCancelled Whether to pass cancelled events or not
     */
    fun registerEvent(
        event: Class<out MachineEvent>,
        listener: MachineListener,
        priority: MachineEventPriority,
        executor: MachineEventExecutor,
        ignoreCancelled: Boolean
    )

    /**
     * Registers the given event to a listener wrapping [callback]
     * 
     * @param event Event type to register
     * @param priority Priority to register this event at
     * @param ignoreCancelled Whether to pass cancelled events or not
     * @param callback Callback to register
     */
    fun <T : MachineEvent> registerListener(
        event: Class<T>,
        priority: MachineEventPriority,
        ignoreCancelled: Boolean,
        callback: BiConsumer<T, MachineListener>
    ): MachineListener

    /**
     * Registers the given event to a listener wrapping [callback]
     * 
     * @param event Event type to register
     * @param callback Callback to register
     */
    fun <T : MachineEvent> registerListener(event: Class<T>, callback: BiConsumer<T, MachineListener>): MachineListener =
        registerListener(event, MachineEventPriority.NORMAL, true, callback)

    /**
     * Calls an event with the given details
     *
     * @param event Event details
     * @return False if the event is cancelled. Always true if the event is not [MachineCancellable].
     * @throws IllegalStateException Thrown when an asynchronous event is
     *     fired from synchronous code.
     *     <i>Note: This is best-effort basis, and should not be used to test
     *     synchronized state. This is an indicator for flawed flow logic.</i>
     */
    @Throws(IllegalStateException::class)
    fun callEvent(event: MachineEvent): Boolean
    
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
     * Check if [type] has the [lifecycle].
     */
    fun <T : IMachineComponent> hasComponentLifecycle(type: Class<T>, lifecycle: MachineComponentLifecycle): Boolean

    /**
     * Execute the specified lifecycle methods of all components
     */
    fun doLifecycle(lifecycle: MachineComponentLifecycle, context: IMachineContext?)

    /**
     * Execute the specified lifecycle methods of all components
     */
    fun doLifecycle(lifecycle: MachineComponentLifecycle) {
        doLifecycle(lifecycle, null)
    }
    
    fun <T : MachineCoordinateEvent> bindCoordinateEvent(coordinate: Coordinate3D, event: Class<T>)
    
    fun <T : MachineCoordinateEvent> unbindCoordinateEvent(coordinate: Coordinate3D, event: Class<T>)
    
    fun getCoordinateEventCoordinates(): Set<Coordinate3D>
    
    fun getCoordinateEvents(coordinate: Coordinate3D): Set<Class<out MachineCoordinateEvent>>

}
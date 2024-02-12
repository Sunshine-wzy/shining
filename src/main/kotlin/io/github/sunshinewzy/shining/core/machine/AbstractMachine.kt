package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineContext
import io.github.sunshinewzy.shining.api.machine.IMachineWrench
import io.github.sunshinewzy.shining.api.machine.MachineProperty
import io.github.sunshinewzy.shining.api.machine.component.IMachineComponent
import io.github.sunshinewzy.shining.api.machine.component.MachineComponentLifecycle
import io.github.sunshinewzy.shining.api.machine.component.MachineComponentLifecycle.*
import io.github.sunshinewzy.shining.api.machine.event.*
import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate3D
import io.github.sunshinewzy.shining.core.blueprint.BlueprintClass
import io.github.sunshinewzy.shining.core.machine.structure.SingleMachineStructure
import org.bukkit.Bukkit
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.logging.Level

abstract class AbstractMachine(
    override val property: MachineProperty,
    override var structure: IMachineStructure
) : IMachine {

    override val blueprint: IBlueprintClass = BlueprintClass()

    private val componentRegistry: MutableMap<Class<out IMachineComponent>, IMachineComponent> = ConcurrentHashMap()
    private val componentLifecycles: MutableMap<Class<out IMachineComponent>, MutableSet<MachineComponentLifecycle>> = HashMap()
    private val componentLifecycleRegistry: MutableMap<MachineComponentLifecycle, MutableSet<Class<out IMachineComponent>>> = EnumMap(MachineComponentLifecycle::class.java)
    private val coordinateToEvents: MutableMap<Coordinate3D, MutableSet<Class<out MachineCoordinateEvent>>> = ConcurrentHashMap()
    
    constructor() : this(MachineProperty(NamespacedId.NULL, "null"), SingleMachineStructure())
    

    override fun register(wrench: IMachineWrench): AbstractMachine {
        register()
        wrench.registerMachine(this)
        return this
    }

    override fun register(): AbstractMachine {
        MachineRegistry.registerMachine(this)
        return this
    }

    override fun registerEvents(listener: MachineListener) {
        for ((key, value) in MachineEventUtils.createRegisteredListeners(listener, this).entries) {
            getEventListeners(getRegistrationClass(key)).registerAll(value)
        }
    }

    override fun registerEvent(
        event: Class<out MachineEvent>,
        listener: MachineListener,
        priority: MachineEventPriority,
        executor: MachineEventExecutor,
        ignoreCancelled: Boolean
    ) {
        getEventListeners(event).register(MachineRegisteredListener(listener, executor, priority, this, ignoreCancelled))
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : MachineEvent> registerListener(
        event: Class<T>,
        priority: MachineEventPriority,
        ignoreCancelled: Boolean,
        callback: BiConsumer<T, MachineListener>
    ): MachineListener {
        val listener = CallbackMachineListener(event) { machineEvent, machineListener->
            callback.accept(machineEvent as T, machineListener)
        }
        registerEvent(event, listener, priority, listener, ignoreCancelled)
        return listener
    }

    override fun callEvent(event: MachineEvent): Boolean {
        if (event.isAsynchronous) {
            check(!Thread.holdsLock(this)) { event.getEventName() + " cannot be triggered asynchronously from inside synchronized code." }
            check(!Bukkit.isPrimaryThread()) { event.getEventName() + " cannot be triggered asynchronously from primary server thread." }
        } else {
            check(Bukkit.isPrimaryThread()) { event.getEventName() + " cannot be triggered asynchronously from another thread." }
        }

        fireEvent(event)
        if (event is MachineCancellable) {
            return !event.isCancelled
        }
        return true
    }

    private fun fireEvent(event: MachineEvent) {
        val handlers = event.getHandlers()
        val listeners = handlers.registeredListeners

        for (registration in listeners) {
            // TODO: machine world-settings
//            if (!registration.machine.isEnabled) {
//                continue
//            }
            if (registration.machine !== this)
                continue
            
            try {
                registration.callEvent(event)
            } catch (th: Throwable) {
                Shining.plugin.logger.log(
                    Level.SEVERE,
                    "Could not pass event " + event.getEventName() + " to machine " + registration.machine.property.id,
                    th
                )
            }
        }
    }

    override fun <T : IMachineComponent> getComponent(type: Class<T>): T =
        getComponentOrNull(type) ?: throw IllegalArgumentException("The machine does not have a component of type '$type'.")

    @Suppress("UNCHECKED_CAST")
    override fun <T : IMachineComponent> getComponentOrNull(type: Class<T>): T? =
        componentRegistry[type] as? T

    override fun <T : IMachineComponent> addComponent(type: Class<T>, component: T): T {
        componentRegistry[type] = component
        
        val lifecycles = scanComponentLifecycleMethods(type)
        componentLifecycles[type] = lifecycles
        
        lifecycles.forEach { lifecycle ->
            componentLifecycleRegistry
                .getOrPut(lifecycle) { HashSet() }
                .add(type)
        }
        
        if (hasComponentLifecycle(type, LOAD))
            component.onLoad()
        return component
    }

    override fun <T : IMachineComponent> addComponent(type: Class<T>): T {
        val component = type.getConstructor(IMachine::class.java).newInstance(this) as T
        return addComponent(type, component)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : IMachineComponent> removeComponent(type: Class<T>): T? {
        val component = componentRegistry.remove(type) as? T ?: return null
        if (hasComponentLifecycle(type, DESTROY))
            component.onDestroy()
        componentLifecycles.remove(type)?.forEach { lifecycle ->
            componentLifecycleRegistry[lifecycle]?.remove(type)
        }
        return component
    }

    override fun <T : IMachineComponent> hasComponent(type: Class<T>): Boolean =
        componentRegistry.containsKey(type)

    override fun <T : IMachineComponent> hasComponentLifecycle(type: Class<T>, lifecycle: MachineComponentLifecycle): Boolean {
        val lifecycles = componentLifecycles[type] ?: return false
        return lifecycles.contains(lifecycle)
    }

    override fun doLifecycle(lifecycle: MachineComponentLifecycle, context: IMachineContext?) {
        componentLifecycleRegistry[lifecycle]?.forEach { type ->
            val component = getComponent(type)
            when (lifecycle) {
                LOAD -> component.onLoad()
                ENABLE -> component.onEnable()
                ACTIVATE -> component.onActivate(context!!)
                UPDATE -> component.onUpdate(context!!)
                DEACTIVATE -> component.onDeactivate(context!!)
                DISABLE -> component.onDisable()
                DESTROY -> component.onDestroy()
            }
        }
    }

    override fun <T : MachineCoordinateEvent> bindCoordinateEvent(coordinate: Coordinate3D, event: Class<T>) {
        coordinateToEvents.getOrPut(coordinate) { ConcurrentHashMap.newKeySet() }.add(event)
    }

    override fun <T : MachineCoordinateEvent> unbindCoordinateEvent(coordinate: Coordinate3D, event: Class<T>) {
        coordinateToEvents[coordinate]?.remove(event)
    }

    override fun getCoordinateEventCoordinates(): Set<Coordinate3D> =
        coordinateToEvents.keys

    override fun getCoordinateEvents(coordinate: Coordinate3D): Set<Class<out MachineCoordinateEvent>> =
        coordinateToEvents[coordinate] ?: emptySet()

    private fun <T : IMachineComponent> scanComponentLifecycleMethods(type: Class<T>): HashSet<MachineComponentLifecycle> {
        val lifecycles = HashSet<MachineComponentLifecycle>()
        val declaredMethods = type.javaClass.declaredMethods
        for (declaredMethod in declaredMethods) {
            machineComponentLifecycleMethodNames[declaredMethod.name]?.let { lifecycle ->
                val parameterTypes = declaredMethod.parameterTypes
                if (lifecycle.parameterTypes.size != parameterTypes.size) return@let
                for (i in lifecycle.parameterTypes.indices) {
                    if (lifecycle.parameterTypes[i] != parameterTypes[i])
                        return@let
                }
                lifecycles += lifecycle
            }
        }
        
        if (lifecycles.size != machineComponentLifecycleMethodNames.size) {
            scanParentComponentLifecycleMethods(type.superclass, lifecycles)
        }
        return lifecycles
    }
    
    private fun scanParentComponentLifecycleMethods(type: Class<*>?, lifecycles: HashSet<MachineComponentLifecycle>) {
        if (type == null || type == IMachineComponent::class.java || type == Any::class.java) return

        val declaredMethods = type.javaClass.declaredMethods
        for (declaredMethod in declaredMethods) {
            machineComponentLifecycleMethodNames[declaredMethod.name]?.let { lifecycle ->
                if (lifecycles.contains(lifecycle)) return@let
                
                val parameterTypes = declaredMethod.parameterTypes
                if (lifecycle.parameterTypes.size != parameterTypes.size) return@let
                for (i in lifecycle.parameterTypes.indices) {
                    if (lifecycle.parameterTypes[i] != parameterTypes[i])
                        return@let
                }
                lifecycles += lifecycle
            }
        }

        if (lifecycles.size != machineComponentLifecycleMethodNames.size) {
            scanParentComponentLifecycleMethods(type.superclass, lifecycles)
        }
    }

    private fun getEventListeners(type: Class<out MachineEvent>): MachineHandlerList {
        return try {
            val method = getRegistrationClass(type).getDeclaredMethod("getHandlerList")
            method.setAccessible(true)
            if (!Modifier.isStatic(method.modifiers)) {
                throw IllegalAccessException("getHandlerList must be static")
            }
            method.invoke(null) as MachineHandlerList
        } catch (e: Exception) {
            throw IllegalStateException("Error while registering listener for event type $type: $e")
        }
    }

    private fun getRegistrationClass(clazz: Class<out MachineEvent>): Class<out MachineEvent> {
        return try {
            clazz.getDeclaredMethod("getHandlerList")
            clazz
        } catch (ex: NoSuchMethodException) {
            if (clazz.superclass != null && clazz.superclass != MachineEvent::class.java
                && MachineEvent::class.java.isAssignableFrom(clazz.superclass)
            ) getRegistrationClass(clazz.superclass.asSubclass(MachineEvent::class.java))
            else throw IllegalStateException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!")
        }
    }
    
    
    companion object {
        val machineComponentLifecycleMethodNames: Map<String, MachineComponentLifecycle> =
            MachineComponentLifecycle.values().associateBy { it.methodName }
    }
    
}
package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.machine.*
import io.github.sunshinewzy.shining.api.machine.component.IMachineComponent
import io.github.sunshinewzy.shining.api.machine.component.MachineComponentLifecycle
import io.github.sunshinewzy.shining.api.machine.component.MachineComponentLifecycle.*
import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.blueprint.BlueprintClass
import io.github.sunshinewzy.shining.core.machine.structure.SingleMachineStructure
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractMachine(
    override val property: MachineProperty,
    override var structure: IMachineStructure
) : IMachine {

    override val blueprint: IBlueprintClass = BlueprintClass()

    private val componentRegistry: MutableMap<Class<out IMachineComponent>, IMachineComponent> = ConcurrentHashMap()
    private val componentLifecycles: MutableMap<Class<out IMachineComponent>, MutableSet<MachineComponentLifecycle>> = HashMap()
    private val componentLifecycleRegistry: MutableMap<MachineComponentLifecycle, MutableSet<Class<out IMachineComponent>>> = EnumMap(MachineComponentLifecycle::class.java)

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
                INTERACT -> component.onInteract(context!! as IMachineInteractContext)
                RUN -> component.onRun(context!! as IMachineRunContext)
                DEACTIVATE -> component.onDeactivate(context!!)
                DISABLE -> component.onDisable()
                DESTROY -> component.onDestroy()
            }
        }
    }
    

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
    
    
    companion object {
        val machineComponentLifecycleMethodNames: Map<String, MachineComponentLifecycle> =
            MachineComponentLifecycle.values().associateBy { it.methodName }
    }
    
}
package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineWrench
import io.github.sunshinewzy.shining.api.machine.MachineProperty
import io.github.sunshinewzy.shining.api.machine.component.IMachineComponent
import io.github.sunshinewzy.shining.api.machine.component.MachineComponentLifeCycle
import io.github.sunshinewzy.shining.api.machine.structure.IMachineStructure
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractMachine(
    override val property: MachineProperty,
    override var structure: IMachineStructure
) : IMachine {

    private val componentRegistry: MutableMap<Class<out IMachineComponent>, IMachineComponent> = ConcurrentHashMap()
    private val componentLifeCycles: MutableMap<Class<out IMachineComponent>, MutableSet<MachineComponentLifeCycle>> = HashMap()
    private val componentLifeCycleRegistry: MutableMap<MachineComponentLifeCycle, MutableSet<Class<out IMachineComponent>>> = EnumMap(MachineComponentLifeCycle::class.java)


    override fun register(wrench: IMachineWrench) {
        register()
        wrench.registerMachine(this)
    }

    override fun register() {
        MachineRegistry.registerMachine(this)
    }

    override fun <T : IMachineComponent> getComponent(type: Class<T>): T =
        getComponentOrNull(type) ?: throw IllegalArgumentException("The machine does not have a component of type '$type'.")

    @Suppress("UNCHECKED_CAST")
    override fun <T : IMachineComponent> getComponentOrNull(type: Class<T>): T? =
        componentRegistry[type] as? T

    override fun <T : IMachineComponent> addComponent(type: Class<T>, component: T): T {
        componentRegistry[type] = component
        
        val lifeCycles = scanComponentLifeCycleMethods(type)
        componentLifeCycles[type] = lifeCycles
        
        lifeCycles.forEach { lifeCycle ->
            componentLifeCycleRegistry
                .getOrPut(lifeCycle) { HashSet() }
                .add(type)
        }
        
        if (hasComponentLifeCycle(type, MachineComponentLifeCycle.LOAD))
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
        if (hasComponentLifeCycle(type, MachineComponentLifeCycle.DESTROY))
            component.onDestroy()
        componentLifeCycles.remove(type)?.forEach { lifeCycle ->
            componentLifeCycleRegistry[lifeCycle]?.remove(type)
        }
        return component
    }

    override fun <T : IMachineComponent> hasComponent(type: Class<T>): Boolean =
        componentRegistry.containsKey(type)

    override fun <T : IMachineComponent> hasComponentLifeCycle(type: Class<T>, lifeCycle: MachineComponentLifeCycle): Boolean {
        val lifeCycles = componentLifeCycles[type] ?: return false
        return lifeCycles.contains(lifeCycle)
    }

    private fun <T : IMachineComponent> scanComponentLifeCycleMethods(type: Class<T>): HashSet<MachineComponentLifeCycle> {
        val lifeCycles = HashSet<MachineComponentLifeCycle>()
        val declaredMethods = type.javaClass.declaredMethods
        for (declaredMethod in declaredMethods) {
            machineComponentLifeCycleMethodNames[declaredMethod.name]?.let { lifeCycle ->
                val parameterTypes = declaredMethod.parameterTypes
                if (lifeCycle.parameterTypes.size != parameterTypes.size) return@let
                for (i in lifeCycle.parameterTypes.indices) {
                    if (lifeCycle.parameterTypes[i] != parameterTypes[i])
                        return@let
                }
                lifeCycles += lifeCycle
            }
        }
        
        if (lifeCycles.size != machineComponentLifeCycleMethodNames.size) {
            scanParentComponentLifeCycleMethods(type.superclass, lifeCycles)
        }
        return lifeCycles
    }
    
    private fun scanParentComponentLifeCycleMethods(type: Class<*>?, lifeCycles: HashSet<MachineComponentLifeCycle>) {
        if (type == null || type == IMachineComponent::class.java || type == Any::class.java) return

        val declaredMethods = type.javaClass.declaredMethods
        for (declaredMethod in declaredMethods) {
            machineComponentLifeCycleMethodNames[declaredMethod.name]?.let { lifeCycle ->
                if (lifeCycles.contains(lifeCycle)) return@let
                
                val parameterTypes = declaredMethod.parameterTypes
                if (lifeCycle.parameterTypes.size != parameterTypes.size) return@let
                for (i in lifeCycle.parameterTypes.indices) {
                    if (lifeCycle.parameterTypes[i] != parameterTypes[i])
                        return@let
                }
                lifeCycles += lifeCycle
            }
        }

        if (lifeCycles.size != machineComponentLifeCycleMethodNames.size) {
            scanParentComponentLifeCycleMethods(type.superclass, lifeCycles)
        }
    }
    
    
    companion object {
        val machineComponentLifeCycleMethodNames: Map<String, MachineComponentLifeCycle> =
            MachineComponentLifeCycle.values().associateBy { it.methodName }
    }
    
}
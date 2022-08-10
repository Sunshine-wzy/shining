package io.github.sunshinewzy.sunstcore.core.machine.manager

import io.github.sunshinewzy.sunstcore.core.machine.IMachine
import io.github.sunshinewzy.sunstcore.core.machine.processor.IMachineRegistrationProcessor
import io.github.sunshinewzy.sunstcore.objects.SLocation
import java.util.concurrent.ConcurrentHashMap

object MachineManager : IMachineManager {
    private val registeredMachines = ConcurrentHashMap.newKeySet<IMachine>()
    private val registeredProcessors = ConcurrentHashMap.newKeySet<IMachineRegistrationProcessor>()
    private val activeMachineMap = ConcurrentHashMap<SLocation, IMachine>()
    
    
    override fun register(machine: IMachine) {
        if(isRegistered(machine)) throw RuntimeException("The machine '${machine.property}' has already been registered.")
        
        registeredMachines += machine
        registeredProcessors.forEach { it.onRegister(machine) }
    }

    override fun unregister(machine: IMachine) {
        registeredMachines -= machine
    }

    override fun isRegistered(machine: IMachine): Boolean {
        return registeredMachines.contains(machine)
    }

    override fun activate(location: SLocation, machine: IMachine) {
        activeMachineMap[location] = machine
    }

    override fun deactivate(location: SLocation) {
        activeMachineMap -= location
    }

    override fun run(location: SLocation) {
        activeMachineMap[location]?.run()
    }

    override fun registerProcessor(processor: IMachineRegistrationProcessor) {
        registeredProcessors += processor
    }

    override fun unregisterProcessor(processor: IMachineRegistrationProcessor) {
        registeredProcessors -= processor
    }

    override fun isProcessorRegistered(processor: IMachineRegistrationProcessor): Boolean {
        return registeredProcessors.contains(processor)
    }

}
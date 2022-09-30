package io.github.sunshinewzy.sunstcore.core.machine

import io.github.sunshinewzy.sunstcore.api.machine.IMachine
import io.github.sunshinewzy.sunstcore.api.machine.IMachineRegistry
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.utils.putElement
import java.util.concurrent.ConcurrentHashMap

object MachineRegistry : IMachineRegistry {

    private val machinesByName: MutableMap<NamespacedId, IMachine> = ConcurrentHashMap()
    private val machinesById: MutableMap<String, MutableList<IMachine>> = ConcurrentHashMap()
    
    
    override fun get(name: NamespacedId): IMachine {
        return getOrNull(name)!!
    }

    override fun getOrNull(name: NamespacedId): IMachine? {
        return machinesByName[name]
    }

    override fun getById(id: String): List<IMachine> {
        return machinesById[id.lowercase()] ?: emptyList()
    }

    override fun registerMachine(machine: IMachine): IMachine {
        return register(machine)
    }

    override fun hasMachine(name: NamespacedId): Boolean {
        return machinesByName.containsKey(name)
    }


    private fun <T: IMachine> register(machine: T): T {
        val name = machine.property.id
        require(name !in machinesByName) { "Duplicate IMachine name: $name" }

        machinesByName[name] = machine
        machinesById.putElement(name.id, machine)

        return machine
    }
    
}
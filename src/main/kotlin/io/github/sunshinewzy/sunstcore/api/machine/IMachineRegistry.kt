package io.github.sunshinewzy.sunstcore.api.machine

import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId

interface IMachineRegistry {

    fun get(name: NamespacedId): IMachine

    fun getOrNull(name: NamespacedId): IMachine?
    
    fun getById(id: String): List<IMachine>
    
    
    fun registerMachine(machine: IMachine): IMachine
    
    fun hasMachine(name: NamespacedId): Boolean
    
}
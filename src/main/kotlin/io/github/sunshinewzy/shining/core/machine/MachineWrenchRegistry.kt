package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachineWrench
import io.github.sunshinewzy.shining.api.machine.IMachineWrenchRegistry
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import java.util.concurrent.ConcurrentHashMap

object MachineWrenchRegistry : IMachineWrenchRegistry {
    
    private val wrenchMap: MutableMap<NamespacedId, IMachineWrench> = ConcurrentHashMap()
    

    override fun get(id: NamespacedId): IMachineWrench = getOrNull(id)!!

    override fun getOrNull(id: NamespacedId): IMachineWrench? = wrenchMap[id]

    override fun getAllWrenches(): List<IMachineWrench> = wrenchMap.values.toList()

    override fun register(wrench: IMachineWrench) {
        val id = wrench.getId()
        require(id !in wrenchMap) { "Duplicate IMachineWrench id: $id" }
        
        wrenchMap[id] = wrench
    }
    
}
package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.namespace.NamespacedId

interface IMachineWrenchRegistry {
    
    fun get(id: NamespacedId): IMachineWrench
    
    fun getOrNull(id: NamespacedId): IMachineWrench?
    
    fun getAllWrenches(): List<IMachineWrench>
    
    fun register(wrench: IMachineWrench)
    
}
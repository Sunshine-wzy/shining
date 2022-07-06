package io.github.sunshinewzy.sunstcore.events.smachine

import io.github.sunshinewzy.sunstcore.modules.machine.SSingleMachine
import org.bukkit.Location
import org.bukkit.event.HandlerList

class SSingleMachineRemoveEvent(sSingleMachine: SSingleMachine, val loc: Location) : SSingleMachineEvent(sSingleMachine) {
    
    override fun getHandlers(): HandlerList = handlerList
    
    
    companion object {
        private val handlerList = HandlerList()
        
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
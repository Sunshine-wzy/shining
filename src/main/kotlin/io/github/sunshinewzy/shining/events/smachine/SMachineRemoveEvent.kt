package io.github.sunshinewzy.shining.events.smachine

import io.github.sunshinewzy.shining.core.machine.legacy.SMachine
import org.bukkit.Location
import org.bukkit.event.HandlerList

class SMachineRemoveEvent(sMachine: SMachine, val loc: Location) : SMachineEvent(sMachine) {
    
    override fun getHandlers(): HandlerList = handlerList
    
    
    
    companion object {
        private val handlerList = HandlerList()
        
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
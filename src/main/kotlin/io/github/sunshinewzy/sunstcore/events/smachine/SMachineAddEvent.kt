package io.github.sunshinewzy.sunstcore.events.smachine

import io.github.sunshinewzy.sunstcore.modules.machine.SMachine
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class SMachineAddEvent(
    sMachine: SMachine,
    val loc: Location,
    val player: Player
) : SMachineEvent(sMachine) {
    
    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()
        
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
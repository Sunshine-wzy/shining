package io.github.sunshinewzy.shining.events.smachine

import io.github.sunshinewzy.shining.core.machine.legacy.SSingleMachine
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class SSingleMachineAddEvent(
    sSingleMachine: SSingleMachine,
    val loc: Location,
    val player: Player
) : SSingleMachineEvent(sSingleMachine) {
    
    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()
        
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
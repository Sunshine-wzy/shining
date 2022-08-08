package io.github.sunshinewzy.sunstcore.events.smachine

import io.github.sunshinewzy.sunstcore.core.machine.legacy.SMachine
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class SMachineUpgradeEvent(
    sMachine: SMachine,
    val loc: Location,
    val player: Player,
    val level: Short
) : SMachineEvent(sMachine) {
    
    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()
        
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
package io.github.sunshinewzy.sunstcore.events.smachine

import io.github.sunshinewzy.sunstcore.core.machine.legacy.SFlatMachine
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class SFlatMachineAddEvent(
    sFlatMachine: SFlatMachine,
    loc: Location,
    val player: Player,
    val face: BlockFace
) : SFlatMachineEvent(sFlatMachine, loc) {
    
    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()
        
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
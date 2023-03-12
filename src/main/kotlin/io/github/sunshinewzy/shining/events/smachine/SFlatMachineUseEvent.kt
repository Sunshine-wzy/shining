package io.github.sunshinewzy.shining.events.smachine

import io.github.sunshinewzy.shining.core.machine.legacy.SFlatMachine
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class SFlatMachineUseEvent(
    sFlatMachine: SFlatMachine,
    loc: Location,
    val player: Player
) : SFlatMachineEvent(sFlatMachine, loc) {

    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
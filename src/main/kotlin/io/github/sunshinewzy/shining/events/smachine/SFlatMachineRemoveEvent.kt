package io.github.sunshinewzy.shining.events.smachine

import io.github.sunshinewzy.shining.core.machine.legacy.SFlatMachine
import org.bukkit.Location
import org.bukkit.event.HandlerList

class SFlatMachineRemoveEvent(sFlatMachine: SFlatMachine, loc: Location) : SFlatMachineEvent(sFlatMachine, loc) {

    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
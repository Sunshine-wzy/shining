package io.github.sunshinewzy.shining.events.smachine

import io.github.sunshinewzy.shining.core.machine.legacy.SSingleMachine
import org.bukkit.Location
import org.bukkit.event.HandlerList

class SSingleMachineRemoveEvent(sSingleMachine: SSingleMachine, val loc: Location) :
    SSingleMachineEvent(sSingleMachine) {

    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
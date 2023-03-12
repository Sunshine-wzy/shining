package io.github.sunshinewzy.shining.events.slocationdata

import io.github.sunshinewzy.shining.objects.SLocation
import org.bukkit.event.HandlerList

class SLocationDataClearEvent(sLocation: SLocation) : SLocationDataEvent(sLocation) {

    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }

}
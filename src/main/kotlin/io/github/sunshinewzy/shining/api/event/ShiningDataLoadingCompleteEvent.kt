package io.github.sunshinewzy.shining.api.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ShiningDataLoadingCompleteEvent : Event() {

    override fun getHandlers(): HandlerList = handlerList
    
    
    companion object {
        private val handlerList = HandlerList()
        
        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
    
}
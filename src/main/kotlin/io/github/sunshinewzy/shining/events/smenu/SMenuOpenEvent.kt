package io.github.sunshinewzy.shining.events.smenu

import io.github.sunshinewzy.shining.utils.menu.SMenu
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class SMenuOpenEvent(
    sMenu: SMenu,
    id: String,
    title: String,
    player: Player,
    page: Int = 0
) : SMenuEvent(sMenu, id, title, player, page) {

    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }

}
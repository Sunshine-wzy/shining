package io.github.sunshinewzy.sunstcore.events.smenu

import io.github.sunshinewzy.sunstcore.objects.SMenu
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class SMenuClickEvent(
    sMenu: SMenu,
    id: String,
    title: String,
    player: Player,
    val slot: Int,
    val buttonName: String,
    val button: ItemStack,
    page: Int = 0
) : SMenuEvent(sMenu, id, title, player, page) {

    override fun getHandlers(): HandlerList = handlerList


    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
    
}
package io.github.sunshinewzy.sunstcore.events.smenu

import io.github.sunshinewzy.sunstcore.core.menu.SMenu
import org.bukkit.entity.Player
import org.bukkit.event.Event

abstract class SMenuEvent(
    val sMenu: SMenu,
    val id: String,
    val title: String,
    val player: Player,
    val page: Int
) : Event()
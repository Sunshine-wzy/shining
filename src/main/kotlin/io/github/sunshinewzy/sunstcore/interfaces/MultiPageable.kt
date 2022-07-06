package io.github.sunshinewzy.sunstcore.interfaces

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface MultiPageable {
    fun pageInvIn(player: Player, page: Int): Inventory
}
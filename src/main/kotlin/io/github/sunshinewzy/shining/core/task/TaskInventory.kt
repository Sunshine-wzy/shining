package io.github.sunshinewzy.shining.core.task

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface TaskInventory {
    fun getTaskInv(player: Player): Inventory

    fun openTaskInv(player: Player, inv: Inventory = getTaskInv(player))
}
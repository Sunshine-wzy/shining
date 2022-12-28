package io.github.sunshinewzy.shining.core.machine.behavior

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

abstract class MachineBehavior {
    
    open fun onInteract(event: PlayerInteractEvent, player: Player, clickedBlock: Block, item: ItemStack, action: Action) {}
    
    
}
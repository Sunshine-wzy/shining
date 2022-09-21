package io.github.sunshinewzy.sunstcore.core.dictionary.item.behavior

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.ItemStack

abstract class ItemBehavior {

    open fun handleInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {}
    open fun handleEntityInteract(event: PlayerInteractAtEntityEvent, player: Player, item: ItemStack, clicked: Entity) {}
    open fun handleBreak(event: PlayerItemBreakEvent, player: Player, item: ItemStack) {}
    open fun handleInventoryClick(event: InventoryClickEvent, player: Player, item: ItemStack) {}
    open fun handleInventoryClickOnCursor(event: InventoryClickEvent, player: Player, item: ItemStack) {}
    open fun handleInventoryHotbarSwap(event: InventoryClickEvent, player: Player, item: ItemStack) {}
    
}
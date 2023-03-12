package io.github.sunshinewzy.shining.core.dictionary.item.behavior

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.ItemStack

abstract class ItemBehavior {

    open fun onInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {}
    open fun onEntityInteract(event: PlayerInteractAtEntityEvent, player: Player, item: ItemStack, clicked: Entity) {}
    open fun onBreak(event: PlayerItemBreakEvent, player: Player, item: ItemStack) {}
    open fun onInventoryClick(event: InventoryClickEvent, player: Player, item: ItemStack) {}
    open fun onInventoryClickOnCursor(event: InventoryClickEvent, player: Player, item: ItemStack) {}
    open fun onInventoryHotbarSwap(event: InventoryClickEvent, player: Player, item: ItemStack) {}

}
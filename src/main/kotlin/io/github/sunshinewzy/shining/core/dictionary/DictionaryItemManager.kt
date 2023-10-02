package io.github.sunshinewzy.shining.core.dictionary

import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isAir

internal object DictionaryItemManager {

    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return

        findBehaviors(item)?.forEach {
            it.onInteract(event, event.player, item, event.action)
        }
    }

    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        val item = event.player.inventory.getItem(event.hand) ?: return

        findBehaviors(item)?.forEach {
            it.onEntityInteract(event, event.player, item, event.rightClicked)
        }
    }

    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerItemBreak(event: PlayerItemBreakEvent) {
        findBehaviors(event.brokenItem)?.forEach {
            it.onBreak(event, event.player, event.brokenItem)
        }
    }

    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        event.currentItem?.let { clickedItem ->
            findBehaviors(clickedItem)?.forEach { it.onInventoryClick(event, player, clickedItem) }
        }
        event.cursor?.let { cursorItem ->
            findBehaviors(cursorItem)?.forEach { it.onInventoryClickOnCursor(event, player, cursorItem) }
        }

        if (event.click == ClickType.NUMBER_KEY) {
            player.inventory.getItem(event.hotbarButton)?.let { hotbarItem ->
                findBehaviors(hotbarItem)?.forEach { it.onInventoryHotbarSwap(event, player, hotbarItem) }
            }
        }
    }


    private fun findBehaviors(item: ItemStack?): List<ItemBehavior>? {
        if (item.isAir()) return null
        return item.dictionaryItem?.getBehaviors()
    }

}
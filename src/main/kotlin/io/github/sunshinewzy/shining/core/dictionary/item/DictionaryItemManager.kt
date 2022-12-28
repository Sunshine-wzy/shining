package io.github.sunshinewzy.shining.core.dictionary.item

import io.github.sunshinewzy.shining.core.dictionary.DictionaryItem.Companion.dictionaryItem
import io.github.sunshinewzy.shining.core.dictionary.item.behavior.ItemBehavior
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

internal object DictionaryItemManager {
    
    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = false)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        findBehaviors(event.item)?.forEach { 
            it.onInteract(event, event.player, event.item!!, event.action)
        }
    }
    
    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        val item = event.player.inventory.getItem(event.hand)
        findBehaviors(item)?.forEach { 
            it.onEntityInteract(event, event.player, item!!, event.rightClicked)
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
        if(player.gameMode == GameMode.CREATIVE) return
        
        val clickedItem = event.currentItem
        val cursorItem = event.cursor

        findBehaviors(clickedItem)?.forEach { it.onInventoryClick(event, player, clickedItem!!) }
        findBehaviors(cursorItem)?.forEach { it.onInventoryClickOnCursor(event, player, cursorItem!!) }

        if(event.click == ClickType.NUMBER_KEY) {
            val hotbarItem = player.inventory.getItem(event.hotbarButton)
            findBehaviors(hotbarItem)?.forEach { it.onInventoryHotbarSwap(event, player, hotbarItem!!) }
        }
    }
    
    
    private fun findBehaviors(item: ItemStack?): List<ItemBehavior>? =
        item?.dictionaryItem?.behaviors
    
}
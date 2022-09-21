package io.github.sunshinewzy.sunstcore.core.dictionary.item

import io.github.sunshinewzy.sunstcore.core.dictionary.DictionaryItem.Companion.dictionaryItem
import io.github.sunshinewzy.sunstcore.core.dictionary.item.behavior.ItemBehavior
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

internal object DictionaryItemManager {
    
    @SubscribeEvent(EventPriority.HIGHEST)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if(event.hand != EquipmentSlot.HAND) return
        
        findBehaviors(event.item)?.forEach { 
            it.handleInteract(event, event.player, event.item!!, event.action)
        }
    }
    
    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        val item = event.player.inventory.getItem(event.hand)
        findBehaviors(item)?.forEach { 
            it.handleEntityInteract(event, event.player, item!!, event.rightClicked)
        }
    }
    
    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerItemBreak(event: PlayerItemBreakEvent) {
        findBehaviors(event.brokenItem)?.forEach { 
            it.handleBreak(event, event.player, event.brokenItem)
        }
    }
    
    @SubscribeEvent(EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val clickedItem = event.currentItem
        val cursorItem = event.cursor

        findBehaviors(clickedItem)?.forEach { it.handleInventoryClick(event, player, clickedItem!!) }
        findBehaviors(cursorItem)?.forEach { it.handleInventoryClickOnCursor(event, player, cursorItem!!) }

        if(event.click == ClickType.NUMBER_KEY) {
            val hotbarItem = player.inventory.getItem(event.hotbarButton)
            findBehaviors(hotbarItem)?.forEach { it.handleInventoryHotbarSwap(event, player, hotbarItem!!) }
        }
    }
    
    
    private fun findBehaviors(item: ItemStack?): List<ItemBehavior>? =
        item?.dictionaryItem?.behaviors
    
}
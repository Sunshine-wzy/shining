package io.github.sunshinewzy.shining.listeners

import io.github.sunshinewzy.shining.interfaces.Initable
import io.github.sunshinewzy.shining.objects.inventoryholder.SCraftInventoryHolder
import io.github.sunshinewzy.shining.objects.inventoryholder.SPartProtectInventoryHolder
import io.github.sunshinewzy.shining.objects.inventoryholder.SProtectInventoryHolder
import io.github.sunshinewzy.shining.utils.giveItem
import io.github.sunshinewzy.shining.utils.subscribeEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

object SunSTSubscriber : Initable {
    override fun init() {
        // 保护 holder 为 SProtectInventoryHolder 的物品栏
        subscribeEvent<InventoryClickEvent>(EventPriority.HIGHEST) {
            val holder = inventory.holder ?: return@subscribeEvent

            when (holder) {
                is SProtectInventoryHolder<*> -> {
                    isCancelled = true
                }

                is SPartProtectInventoryHolder<*> -> {
                    isCancelled = true

                    when (click) {
                        ClickType.LEFT, ClickType.RIGHT, ClickType.CREATIVE, ClickType.SHIFT_LEFT -> {}
                        else -> return@subscribeEvent
                    }

                    clickedInventory?.holder?.let { topHolder ->
                        if (topHolder is SCraftInventoryHolder<*>) {
                            if (slot == topHolder.outputSlot) {
                                isCancelled = false
                                return@subscribeEvent
                            }
                        }

                        if (topHolder is SPartProtectInventoryHolder<*>) {
                            if (topHolder.allowClickSlots.contains(slot))
                                isCancelled = false

                            return@subscribeEvent
                        }
                    }

                    if (click != ClickType.SHIFT_LEFT) {
                        isCancelled = false
                        return@subscribeEvent
                    }
                }
            }
        }

        subscribeEvent<InventoryDragEvent>(EventPriority.HIGHEST) {
            val holder = inventory.holder ?: return@subscribeEvent

            when (holder) {
                is SProtectInventoryHolder<*> -> {
                    isCancelled = true
                }

                is SPartProtectInventoryHolder<*> -> {
                    rawSlots.forEach {
                        if (!holder.allowClickSlots.contains(it)) {
                            isCancelled = true
                            return@subscribeEvent
                        }
                    }
                }
            }
        }

        subscribeEvent<InventoryCloseEvent> {
            val inv = view.topInventory
            val holder = inv.holder ?: return@subscribeEvent

            if (holder is SCraftInventoryHolder<*>) {
                val player = player as? Player ?: return@subscribeEvent

                holder.allowClickSlots.forEach { order ->
                    inv.getItem(order)?.let {
                        if (it.type != Material.AIR)
                            player.giveItem(it)
                    }
                }
            }
        }

    }
}
package io.github.sunshinewzy.shining.core.guide.context

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.type.Basic

class GuideShortcutBarContext : AbstractGuideContextElement(GuideShortcutBarContext) {
    
    private val slots: Array<ItemStack> = Array(9) { ItemStack(Material.AIR) }
    
    
    fun update(menu: Basic) {
        for (i in range) {
            menu.set((i + 1) orderWith 6, slots[i])
        }
    }
    
    fun setItem(index: Int, item: ItemStack) {
        if (index in range) {
            slots[index] = item
        }
    }
    
    fun setItems(items: Collection<ItemStack>, start: Int = 0, end: Int = 8) {
        var i = start
        for (item in items) {
            if (i in range && i <= end) {
                slots[i] = item
            } else break
            i++
        }
    }
    
    fun setItems(items: Array<ItemStack>, start: Int = 0, end: Int = 8) {
        var i = start
        for (item in items) {
            if (i in range && i <= end) {
                slots[i] = item
            } else break
            i++
        }
    }
    
    
    companion object Key : GuideContext.Key<GuideShortcutBarContext> {
        private val range = 0 until 9
    }
    
}
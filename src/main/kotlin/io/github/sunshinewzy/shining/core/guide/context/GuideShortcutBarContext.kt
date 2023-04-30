package io.github.sunshinewzy.shining.core.guide.context

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isNotAir

class GuideShortcutBarContext : AbstractGuideContextElement(GuideShortcutBarContext) {
    
    private val slots: Array<ItemStack> = Array(5) { AIR }
    
    
    fun update(menu: Basic) {
        for (i in RANGE) {
            if (slots[i].isNotAir())
                menu.set((i + 3) orderWith 6, slots[i])
        }
    }
    
    fun setItem(index: Int, item: ItemStack) {
        if (index in RANGE) {
            slots[index] = item
        }
    }
    
    fun setItems(items: Collection<ItemStack>, start: Int = 0, end: Int = 4) {
        if (start > 0) {
            for (i in 0 until start) {
                slots[i] = AIR
            }
        }
        
        var i = start
        for (item in items) {
            if (i in RANGE && i <= end) {
                slots[i] = item
            } else break
            i++
        }
        
        if (i in RANGE) {
            for (j in i until 5) {
                slots[j] = AIR
            }
        }
    }
    
    fun setItems(items: Array<ItemStack>, start: Int = 0, end: Int = 4) {
        if (start > 0) {
            for (i in 0 until start) {
                slots[i] = AIR
            }
        }
        
        var i = start
        for (item in items) {
            if (i in RANGE && i <= end) {
                slots[i] = item
            } else break
            i++
        }

        if (i in RANGE) {
            for (j in i until 5) {
                slots[j] = AIR
            }
        }
    }
    
    
    companion object Key : GuideContext.Key<GuideShortcutBarContext> {
        private val RANGE = 0 until 5
        private val AIR = ItemStack(Material.AIR)
    }
    
}
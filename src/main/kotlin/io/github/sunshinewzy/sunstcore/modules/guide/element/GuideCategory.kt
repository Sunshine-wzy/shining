package io.github.sunshinewzy.sunstcore.modules.guide.element

import io.github.sunshinewzy.sunstcore.modules.guide.ElementCondition
import io.github.sunshinewzy.sunstcore.modules.guide.GuideElement
import io.github.sunshinewzy.sunstcore.modules.guide.SGuide
import io.github.sunshinewzy.sunstcore.objects.item.SunSTIcon
import io.github.sunshinewzy.sunstcore.objects.orderWith
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import java.util.*

class GuideCategory(id: String, symbol: ItemStack) : GuideElement(id, symbol) {
    private val elements = LinkedList<GuideElement>()
    
    private val menuBuilder: Linked<GuideElement>.() -> Unit = {
        rows(6)
        slots(SGuide.slotOrders)

        elements { elements }

        val lockedElements = LinkedList<GuideElement>()
        onGenerate { player, element, index, slot ->
            val condition = element.getCondition(player)
            if(condition == ElementCondition.LOCKED_DEPENDENCY || condition == ElementCondition.LOCKED_LOCK)
                lockedElements += element
            element.getSymbolByCondition(player, condition)
        }

        onBuild { inv ->
            SGuide.edgeOrders.forEach {
                inv.setItem(it, SunSTIcon.EDGE.item)
            }
        }

        setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
            if(hasPreviousPage) {
                SunSTIcon.PAGE_PRE_GLASS_PANE.item
            } else SunSTIcon.EDGE.item
        }

        setNextPage(8 orderWith 6) { page, hasNextPage ->
            if(hasNextPage) {
                SunSTIcon.PAGE_NEXT_GLASS_PANE.item
            } else SunSTIcon.EDGE.item
        }

        onClick { event, element ->
            if(element in lockedElements) return@onClick

            element.open(event.clicker, element)
        }
        
        set(2 orderWith 1, SunSTIcon.BACK.item) {
            if(clicker.isSneaking) {
                SGuide.open(clicker)
            } else {
                back(clicker)
            }
        }
    }
    
    
    override fun openAction(player: Player) {
        player.openMenu(SGuide.TITLE, menuBuilder)
    }
    
    
    fun registerElement(element: GuideElement) {
        elements += element
    }
    
}
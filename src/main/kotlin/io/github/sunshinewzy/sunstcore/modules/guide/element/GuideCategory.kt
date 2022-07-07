package io.github.sunshinewzy.sunstcore.modules.guide.element

import io.github.sunshinewzy.sunstcore.modules.guide.GuideElement
import io.github.sunshinewzy.sunstcore.modules.guide.SGuide
import io.github.sunshinewzy.sunstcore.objects.item.GuideIcon
import io.github.sunshinewzy.sunstcore.objects.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import java.util.*

class GuideCategory(name: String, symbol: ItemStack) : GuideElement(name, symbol) {
    private val elements = LinkedList<GuideElement>()
    
    private val menuBuilder: Linked<GuideElement>.() -> Unit = {
        rows(6)
        slots(SGuide.slotOrders)

        elements { elements }

        val lockedElements = LinkedList<GuideElement>()
        onGenerate { player, element, index, slot ->
            val item = element.getConditionSymbol(player)
            if(item.type == Material.BARRIER) {
                item.itemMeta?.lore?.firstOrNull()?.let {
                    if(LOCKED_TEXT == it)
                        lockedElements += element
                }
            }
            item
        }

        onBuild { inv ->
            SGuide.edgeOrders.forEach {
                inv.setItem(it, GuideIcon.EDGE.item)
            }
        }

        setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
            if(hasPreviousPage) {
                GuideIcon.PAGE_PRE_GLASS_PANE.item
            } else GuideIcon.EDGE.item
        }

        setNextPage(8 orderWith 6) { page, hasNextPage ->
            if(hasNextPage) {
                GuideIcon.PAGE_NEXT_GLASS_PANE.item
            } else GuideIcon.EDGE.item
        }

        onClick { event, element ->
            if(element in lockedElements) return@onClick

            element.open(event.clicker, element)
        }
    }
    
    
    override fun openAction(player: Player) {
        player.openMenu(SGuide.TITLE, menuBuilder)
    }
    
}
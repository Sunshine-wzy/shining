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
    
    
    override fun openAction(player: Player) {
        player.openMenu<Linked<GuideElement>>(SGuide.TITLE) {
            rows(6)
            slots(SGuide.slotOrders)

            elements { elements }

            val dependencyLockedElements = LinkedList<GuideElement>()
            val lockLockedElements = LinkedList<GuideElement>()
            onGenerate { player, element, index, slot ->
                val condition = element.getCondition(player)
                if(condition == ElementCondition.LOCKED_DEPENDENCY)
                    dependencyLockedElements += element
                else if(condition == ElementCondition.LOCKED_LOCK)
                    lockLockedElements += element
                element.getSymbolByCondition(player, condition)
            }

            onBuild(onBuild = SGuide.onBuildEdge)

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
                if(element in dependencyLockedElements) return@onClick
                
                if(element in lockLockedElements) {
                    if(element.unlock(player)) {
                        SGuide.fireworkCongratulate(player)
                        open(player)
                    }
                    
                    return@onClick
                }

                element.open(event.clicker, this@GuideCategory)
            }

            set(2 orderWith 1, SunSTIcon.BACK.item) {
                if(clickEvent().isShiftClick) {
                    SGuide.open(clicker)
                } else {
                    back(clicker)
                }
            }
        }
    }
    
    
    fun registerElement(element: GuideElement) {
        elements += element
    }
    
    
}
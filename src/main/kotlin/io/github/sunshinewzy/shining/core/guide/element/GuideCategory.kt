package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.core.guide.ElementCondition
import io.github.sunshinewzy.shining.core.guide.GuideElement
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import java.util.*

/**
 * Represent a category, which is showed in the guide.
 *
 * @param id to identify this [GuideCategory]
 * @param symbol to display this [GuideCategory] in guide
 * @param tier higher tier will make this [GuideCategory] appear further down in the guide
 */
class GuideCategory(id: String, symbol: ItemStack, var tier: Int = 0) : GuideElement(id, symbol) {
    private val elements = LinkedList<GuideElement>()
    
    
    override fun openAction(player: Player, team: GuideTeam) {
        player.openMenu<Linked<GuideElement>>(player.getLangText(ShiningGuide.TITLE)) {
            rows(6)
            slots(ShiningGuide.slotOrders)

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

            onBuild(true, ShiningGuide.onBuildEdge)

            setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
                if(hasPreviousPage) {
                    ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.item
                } else ShiningIcon.EDGE.item
            }

            setNextPage(8 orderWith 6) { page, hasNextPage ->
                if(hasNextPage) {
                    ShiningIcon.PAGE_NEXT_GLASS_PANE.item
                } else ShiningIcon.EDGE.item
            }

            onClick { event, element ->
                if(element in dependencyLockedElements) return@onClick
                
                if(element in lockLockedElements) {
                    if(element.unlock(player)) {
                        ShiningGuide.fireworkCongratulate(player)
                        open(player, team)
                    }
                    
                    return@onClick
                }

                element.open(event.clicker, team, this@GuideCategory)
            }

            set(2 orderWith 1, ShiningIcon.BACK.item) {
                if(clickEvent().isShiftClick) {
                    ShiningGuide.openMainMenu(clicker)
                } else {
                    back(clicker, team)
                }
            }

            set(5 orderWith 1, ShiningIcon.SETTINGS.item) {
                ShiningGuide.openSettings(player, team)
            }
        }
    }
    
    
    fun registerElement(element: GuideElement) {
        elements += element
    }
    
    
}
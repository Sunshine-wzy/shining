package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.element.GuideCategory
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isAir
import java.util.*

class GuideCategoryState(element: GuideCategory) : GuideElementState(element) {

    val elements: MutableList<IGuideElement> = LinkedList()


    override fun openAdvancedEditor(player: Player) {
        player.openMultiPageMenu<IGuideElement>(player.getLangText("menu-shining_guide-editor-state-category-title")) {
            elements { elements }
            
            onGenerate(true) { player, element, _, _ -> 
                element.getUnlockedSymbol(player)
            }
            
            onClick { _, element -> 
                editElement(player, element)
            }
            
            onClick(lock = true) { event ->
                if (event.rawSlot in ShiningGuide.slotOrders && event.currentItem.isAir()) {
                    
                }
            }
        }
    }
    
    fun editElement(player: Player, element: IGuideElement) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-category-element-title")) { 
            rows(3)

            map(
                "-B-------",
                "-    d  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                openAdvancedEditor(player)
            }

            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                elements -= element
                openAdvancedEditor(player)
            }

            onClick(lock = true)
        }
    }

}
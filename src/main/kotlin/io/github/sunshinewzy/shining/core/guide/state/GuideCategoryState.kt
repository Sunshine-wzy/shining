package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.core.guide.element.GuideCategory
import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import java.util.*

class GuideCategoryState(element: GuideCategory) : GuideElementState(element) {

    val elements: MutableList<IGuideElement> = LinkedList()


    override fun openAdvancedEditor(player: Player) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-category-title")) {
            
        }
    }

}
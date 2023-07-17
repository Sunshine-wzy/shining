package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.core.guide.element.GuideItem
import org.bukkit.entity.Player

class GuideItemState @JvmOverloads constructor(element: IGuideElement? = null) : GuideElementState(element) {

    override fun toElement(): IGuideElement =
        GuideItem().also { it.update(this) }

    override fun openAdvancedEditor(player: Player) {
        TODO("Not yet implemented")
    }
    
}
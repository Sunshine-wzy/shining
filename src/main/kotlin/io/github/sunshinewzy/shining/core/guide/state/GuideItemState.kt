package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.core.guide.element.GuideItem
import org.bukkit.entity.Player

class GuideItemState : GuideElementState() {

    
    
    override fun toElement(): GuideItem =
        GuideItem().also { it.update(this) }

    override fun openAdvancedEditor(player: Player) {
        TODO("Not yet implemented")
    }
    
}
package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.item.ConsumableItemGroup
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.element.GuideItem
import org.bukkit.entity.Player

class GuideItemState : GuideElementState() {

    var itemGroup: ConsumableItemGroup? = null
    
    
    
    
    
    override fun toElement(): GuideItem =
        GuideItem().also { it.update(this) }

    override fun clone(): GuideItemState {
        val state = GuideItemState()
        copyTo(state)
        
        state.itemGroup = itemGroup
        return state
    }

    override fun openAdvancedEditor(player: Player, team: GuideTeam, context: GuideContext) {
        TODO("Not yet implemented")
    }
    
}
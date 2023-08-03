package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.item.ConsumableItemGroup
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.element.GuideItem
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
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
        val group = itemGroup ?: ConsumableItemGroup().also { itemGroup = it }
        group.openEditor(player, GuideEditorContext.Back {
            openEditor(player, team, context)
        })
    }
    
}
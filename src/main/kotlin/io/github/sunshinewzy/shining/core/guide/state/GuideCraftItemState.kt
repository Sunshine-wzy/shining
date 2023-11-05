package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.universal.item.UniversalItem
import io.github.sunshinewzy.shining.core.guide.element.GuideCraftItem
import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

class GuideCraftItemState : GuideElementState() {
    
    var craftItem: UniversalItem? = null
    

    override fun openAdvancedEditor(player: Player, team: IGuideTeam, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-craft_item-title")) {
            
        }
    }

    override fun toElement(): GuideCraftItem =
        GuideCraftItem().also { it.update(this) }

    override fun clone(): GuideCraftItemState {
        val state = GuideCraftItemState()
        copyTo(state)
        
        state.craftItem = craftItem?.clone()
        return state
    }
    
}
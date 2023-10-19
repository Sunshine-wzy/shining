package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.core.guide.element.GuideEmpty
import org.bukkit.entity.Player

class GuideEmptyState : GuideElementState() {

    override fun openAdvancedEditor(player: Player, team: IGuideTeam, context: GuideContext) {}

    override fun toElement(): GuideEmpty =
        GuideEmpty().also { it.update(this) }

    override fun clone(): GuideEmptyState {
        val state = GuideEmptyState()
        copyTo(state)
        return state
    }
    
}
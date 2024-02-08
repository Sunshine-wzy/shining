package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.element.GuideEmpty
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest

class GuideEmptyState : GuideElementState() {
    
    var defaultComplete: Boolean = false
    

    override fun openAdvancedEditor(player: Player, team: IGuideTeam, context: GuideContext) {
        player.openMenu<Chest>(player.getLangText("menu-shining_guide-editor-state-empty-title")) { 
            rows(3)

            map(
                "-B-------",
                "-   a   -",
                "---------"
            )
            
            set('-', ShiningIcon.EDGE.item)
            
            set('a', itemDefaultComplete.toOpenOrCloseLocalizedItem(defaultComplete, player)) {
                defaultComplete = !defaultComplete
                openAdvancedEditor(player, team, context)
            }
            
            onBack(player) {
                openEditor(player, team, context)
            }
            
            onClick(lock = true)
        }
    }

    override fun toElement(): GuideEmpty =
        GuideEmpty().also { it.update(this) }

    override fun clone(): GuideEmptyState {
        val state = GuideEmptyState()
        copyTo(state)
        
        state.defaultComplete = defaultComplete
        return state
    }
    
    
    companion object {
        private val itemDefaultComplete = NamespacedIdItem(Material.SLIME_BALL, NamespacedId(Shining, "shining_guide-editor-state-empty-default_complete"))
    }
    
}
package io.github.sunshinewzy.shining.core.guide.draft

import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

object ShiningGuideDrafts {
    
    fun openMenu(player: Player) {
        player.openMenu<Basic> { 
            
        }
    }
    
}
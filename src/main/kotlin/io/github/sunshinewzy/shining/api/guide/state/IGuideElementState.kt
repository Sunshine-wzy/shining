package io.github.sunshinewzy.shining.api.guide.state

import org.bukkit.entity.Player

interface IGuideElementState {
    
    fun update(): Boolean
    
    fun openEditor(player: Player)
    
}
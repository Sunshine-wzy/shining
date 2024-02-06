package io.github.sunshinewzy.shining.api.blueprint

import org.bukkit.entity.Player

interface IBlueprintEditor {
    
    fun open(player: Player, blueprint: IBlueprintClass?)
    
    fun open(player: Player) {
        open(player, null)
    }
    
}
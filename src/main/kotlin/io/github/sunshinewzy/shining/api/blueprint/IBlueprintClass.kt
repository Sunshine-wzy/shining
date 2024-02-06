package io.github.sunshinewzy.shining.api.blueprint

import org.bukkit.entity.Player

interface IBlueprintClass {
    
    fun getNodeTrees(): ArrayList<IBlueprintNodeTree>
    
    fun edit(player: Player)
    
}
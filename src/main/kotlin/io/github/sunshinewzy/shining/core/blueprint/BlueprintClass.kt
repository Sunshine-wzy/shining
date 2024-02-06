package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNodeTree
import org.bukkit.entity.Player

class BlueprintClass : IBlueprintClass {
    
    private val nodeTrees: ArrayList<IBlueprintNodeTree> = ArrayList()
    

    override fun getNodeTrees(): ArrayList<IBlueprintNodeTree> = nodeTrees

    override fun edit(player: Player) {
        
    }
    
}
package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintClass
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintEditor
import org.bukkit.entity.Player

object BlueprintEditor : IBlueprintEditor {

    override fun open(player: Player, blueprint: IBlueprintClass?) {
        if (blueprint == null) {
            val bp = BlueprintClass()
            bp.edit(player)
        } else {
            blueprint.edit(player)
        }
    }
    
}
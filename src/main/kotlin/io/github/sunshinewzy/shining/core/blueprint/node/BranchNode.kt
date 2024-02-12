package io.github.sunshinewzy.shining.core.blueprint.node

import io.github.sunshinewzy.shining.core.blueprint.AbstractBlueprintNode
import io.github.sunshinewzy.shining.core.blueprint.IBlueprintLangNode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest

open class BranchNode : AbstractBlueprintNode(2), IBlueprintLangNode {

    override fun onExecute() {
        
    }

    override fun onEdit(player: Player) {
        player.openMenu<Chest> { 
            rows(3)
            map(
                "---------",
                "-       -",
                "---------"
            )
            
            
        }
    }

    override fun getIcon(): ItemStack = ItemStack(Material.OAK_FENCE_GATE)

    override fun getLanguageNode(): String = "text-blueprint-node-branch"
    
}
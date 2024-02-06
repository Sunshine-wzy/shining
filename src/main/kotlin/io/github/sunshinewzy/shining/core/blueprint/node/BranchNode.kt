package io.github.sunshinewzy.shining.core.blueprint.node

import io.github.sunshinewzy.shining.core.blueprint.AbstractBlueprintNode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class BranchNode : AbstractBlueprintNode(2) {

    override fun getIcon(): ItemStack = ItemStack(Material.OAK_FENCE_GATE)
    
}
package io.github.sunshinewzy.shining.core.blueprint.node

import io.github.sunshinewzy.shining.core.blueprint.AbstractBlueprintNode
import io.github.sunshinewzy.shining.core.blueprint.IBlueprintLangNode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class BranchNode : AbstractBlueprintNode(2), IBlueprintLangNode {

    override fun getIcon(): ItemStack = ItemStack(Material.OAK_FENCE_GATE)

    override fun getLanguageNode(): String = "text-blueprint-node-branch"
    
}
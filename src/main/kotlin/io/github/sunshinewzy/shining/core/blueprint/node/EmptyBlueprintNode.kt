package io.github.sunshinewzy.shining.core.blueprint.node

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode
import io.github.sunshinewzy.shining.core.blueprint.IBlueprintLangNode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object EmptyBlueprintNode : IBlueprintLangNode {

    override fun onExecute() {}

    override fun onEdit() {}

    override fun getIcon(): ItemStack = ItemStack(Material.GLASS_PANE)

    override fun getLanguageNode(): String = "text-blueprint-node-empty"

    override fun getSuccessorAmount(): Int = 0

    override fun getSuccessors(): Array<IBlueprintNode> = emptyArray()

    override fun getPredecessorOrNull(): IBlueprintNode? = null

    override fun setPredecessor(node: IBlueprintNode): IBlueprintNode? = null
    
}
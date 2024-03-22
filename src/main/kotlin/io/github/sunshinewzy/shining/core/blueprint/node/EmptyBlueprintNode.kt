package io.github.sunshinewzy.shining.core.blueprint.node

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode
import io.github.sunshinewzy.shining.core.blueprint.IBlueprintLangNode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

open class EmptyBlueprintNode : IBlueprintLangNode {

    private val successors: Array<IBlueprintNode> = emptyArray()
    private var predecessor: IBlueprintNode? = null
    

    override fun onExecute() {}

    override fun onEdit(player: Player) {}

    override fun getIcon(): ItemStack = ItemStack(Material.GLASS_PANE)

    override fun getLanguageNode(): String = "text-blueprint-node-empty"

    override fun getSuccessorAmount(): Int = 0

    override fun getSuccessors(): Array<IBlueprintNode> = successors

    override fun getPredecessorOrNull(): IBlueprintNode? = predecessor

    override fun setPredecessor(node: IBlueprintNode?): IBlueprintNode? {
        val pre = predecessor
        predecessor = node
        return pre
    }

    override fun instantiate(): IBlueprintNode = EmptyBlueprintNode()
    
}
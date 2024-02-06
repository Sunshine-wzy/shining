package io.github.sunshinewzy.shining.api.blueprint

import org.bukkit.inventory.ItemStack

/**
 * The basic unit of blueprints
 */
interface IBlueprintNode {

    /**
     * Executed when the node is executed.
     */
    fun onExecute()

    /**
     * Executed when the node is edited.
     */
    fun onEdit()

    /**
     * Gets the icon of the node.
     */
    fun getIcon(): ItemStack

    /**
     * Gets the amount of successor nodes.
     */
    fun getSuccessorAmount(): Int

    /**
     * Gets all successor nodes.
     * The size of the array is [getSuccessorAmount].
     */
    fun getSuccessors(): Array<IBlueprintNode>

    /**
     * Gets the first successor node.
     */
    fun getSuccessorOrNull(): IBlueprintNode? {
        val successors = getSuccessors()
        return if (successors.isEmpty()) null else successors[0]
    }

    /**
     * Gets the first successor node.
     * Throws an exception when there is no successor node.
     */
    fun getSuccessor(): IBlueprintNode = getSuccessors()[0]

    /**
     * Gets the predecessor node.
     */
    fun getPredecessorOrNull(): IBlueprintNode?

    /**
     * Gets the predecessor node.
     * Throws an exception when there is no predecessor node.
     */
    fun getPredecessor(): IBlueprintNode = getPredecessorOrNull()!!

    /**
     * Sets the predecessor node.
     * 
     * @return The previous predecessor node, or null if it does not exist
     */
    fun setPredecessor(node: IBlueprintNode): IBlueprintNode?
    
}
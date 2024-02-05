package io.github.sunshinewzy.shining.api.blueprint

/**
 * The logical unit of blueprints
 */
interface IBlueprintNodeTree {

    /**
     * Gets the root of the node tree.
     */
    fun getRootOrNull(): IBlueprintNode?

    /**
     * Gets the root of the node tree.
     * Throws an exception when the root does not exist.
     */
    fun getRoot(): IBlueprintNode = getRootOrNull()!!

    /**
     * Sets the root of the node tree.
     * 
     * @return The previous predecessor node, or null if it does not exist
     */
    fun setRoot(node: IBlueprintNode): IBlueprintNode?
    
}
package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode

object EmptyBlueprintNode : IBlueprintNode {

    override fun onExecute() {}

    override fun onEdit() {}

    override fun getSuccessorAmount(): Int = 0

    override fun getSuccessors(): Array<IBlueprintNode> = emptyArray()

    override fun getPredecessorOrNull(): IBlueprintNode? = null

    override fun setPredecessor(node: IBlueprintNode): IBlueprintNode? = null
    
}
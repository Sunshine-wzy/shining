package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode
import io.github.sunshinewzy.shining.core.blueprint.node.EmptyBlueprintNode

abstract class AbstractBlueprintNode(private val successorAmount: Int = 1) : IBlueprintNode {

    private val successors: Array<IBlueprintNode> = Array(successorAmount) { EmptyBlueprintNode }
    private var predecessor: IBlueprintNode? = null


    override fun onExecute() {}

    override fun onEdit() {}

    override fun getSuccessorAmount(): Int = successorAmount

    override fun getSuccessors(): Array<IBlueprintNode> = successors

    override fun getPredecessorOrNull(): IBlueprintNode? = predecessor

    override fun setPredecessor(node: IBlueprintNode): IBlueprintNode? {
        val pre = predecessor
        predecessor = node
        return pre
    }
    
}
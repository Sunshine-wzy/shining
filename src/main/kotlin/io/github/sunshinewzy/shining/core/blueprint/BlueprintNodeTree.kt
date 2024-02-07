package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNodeTree
import io.github.sunshinewzy.shining.core.blueprint.node.EmptyBlueprintNode

open class BlueprintNodeTree : IBlueprintNodeTree {
    
    private var root: IBlueprintNode = EmptyBlueprintNode


    override fun getRoot(): IBlueprintNode = root

    override fun setRoot(node: IBlueprintNode): IBlueprintNode {
        val pre = root
        root = node
        return pre
    }
    
}
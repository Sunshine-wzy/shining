package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNodeTree

open class BlueprintNodeTree : IBlueprintNodeTree {
    
    private var root: IBlueprintNode? = null


    override fun getRootOrNull(): IBlueprintNode? = root

    override fun setRoot(node: IBlueprintNode): IBlueprintNode? {
        val pre = root
        root = node
        return pre
    }
    
}
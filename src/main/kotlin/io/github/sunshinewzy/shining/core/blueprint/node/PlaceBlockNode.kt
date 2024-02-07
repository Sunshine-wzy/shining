package io.github.sunshinewzy.shining.core.blueprint.node

import io.github.sunshinewzy.shining.core.blueprint.AbstractBlueprintLangNode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class PlaceBlockNode : AbstractBlueprintLangNode() {

    override fun getIcon(): ItemStack = ItemStack(Material.GRASS_BLOCK)

    override fun getLanguageNode(): String = "text-blueprint-node-block-place"
    
}
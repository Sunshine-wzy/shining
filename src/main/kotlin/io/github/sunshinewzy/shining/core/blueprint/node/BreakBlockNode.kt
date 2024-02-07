package io.github.sunshinewzy.shining.core.blueprint.node

import io.github.sunshinewzy.shining.core.blueprint.AbstractBlueprintLangNode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class BreakBlockNode : AbstractBlueprintLangNode() {

    override fun getIcon(): ItemStack = ItemStack(Material.IRON_PICKAXE)

    override fun getLanguageNode(): String = "text-blueprint-node-block-break"
    
}
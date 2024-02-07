package io.github.sunshinewzy.shining.core.blueprint.node

import io.github.sunshinewzy.shining.core.blueprint.AbstractBlueprintLangNode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class SpawnItemNode : AbstractBlueprintLangNode() {

    override fun getIcon(): ItemStack = ItemStack(Material.IRON_INGOT)

    override fun getLanguageNode(): String = "text-blueprint-node-item-spawn"
    
}
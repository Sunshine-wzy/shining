package io.github.sunshinewzy.shining.core.machine.node

import io.github.sunshinewzy.shining.core.blueprint.AbstractBlueprintNode
import io.github.sunshinewzy.shining.core.blueprint.IBlueprintLangNode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class MachineInteractEventNode : AbstractBlueprintNode(), IBlueprintLangNode {

    override fun getIcon(): ItemStack = ItemStack(Material.LEVER)

    override fun getLanguageNode(): String = "text-blueprint-node-event-machine-interact"
    
}
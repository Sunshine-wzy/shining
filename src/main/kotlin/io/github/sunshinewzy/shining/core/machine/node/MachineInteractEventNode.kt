package io.github.sunshinewzy.shining.core.machine.node

import io.github.sunshinewzy.shining.core.blueprint.AbstractBlueprintNode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class MachineInteractEventNode : AbstractBlueprintNode() {

    override fun getIcon(): ItemStack = ItemStack(Material.LEVER)
    
}
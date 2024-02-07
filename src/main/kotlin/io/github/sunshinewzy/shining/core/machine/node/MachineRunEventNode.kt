package io.github.sunshinewzy.shining.core.machine.node

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class MachineRunEventNode : MachineInteractEventNode() {

    override fun getIcon(): ItemStack = ItemStack(Material.REDSTONE_TORCH)

    override fun getLanguageNode(): String = "text-blueprint-node-event-machine-run"
    
}
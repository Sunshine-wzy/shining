package io.github.sunshinewzy.sunstcore.core.machine.block.io

import org.bukkit.inventory.ItemStack

interface MachineOutput {
    
    fun output(item: ItemStack)
    
    fun output(items: List<ItemStack>)
    
    fun output(vararg items: ItemStack) {
        output(items.toList())
    }
    
}
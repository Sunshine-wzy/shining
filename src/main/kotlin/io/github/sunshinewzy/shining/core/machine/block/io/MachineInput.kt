package io.github.sunshinewzy.shining.core.machine.block.io

import org.bukkit.inventory.ItemStack

interface MachineInput {
    
    fun input(item: ItemStack)
    
    fun input(items: List<ItemStack>)
    
    fun input(vararg items: ItemStack) {
        input(items.toList())
    }
    
}
package io.github.sunshinewzy.shining.api.machine

import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface IMachineWrench {

    fun getId(): NamespacedId
    
    fun getItemStack(): ItemStack
    
    fun registerMachine(machine: IMachine)
    
    fun check(location: Location, direction: BlockFace?, player: Player?): Boolean

}
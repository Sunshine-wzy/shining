package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.dictionary.IDictionaryItem
import io.github.sunshinewzy.shining.api.dictionary.behavior.ItemBehavior
import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineWrench
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.core.lang.item.LocalizedItem
import io.github.sunshinewzy.shining.utils.position3D
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object ShiningMachineWrench : IMachineWrench {
    
    private val wrenchItemId = NamespacedId(Shining, "shining_wrench")
    private val wrenchItem = DictionaryRegistry.registerItem(
        wrenchItemId, LocalizedItem(Material.BONE, wrenchItemId),
        object : ItemBehavior() {
            override fun onInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {
                if (event.hand != EquipmentSlot.HAND || action != Action.RIGHT_CLICK_BLOCK) return
                
                val clickedBlock = event.clickedBlock ?: return
                if (clickedBlock.type == Material.AIR) return
                
                check(clickedBlock.location, event.blockFace, player)
            }
        }
    )
    
    private val machineRegistry: MutableList<IMachine> = ArrayList()
    

    override fun registerMachine(machine: IMachine) {
        machineRegistry += machine
    }

    override fun check(location: Location, direction: BlockFace?, player: Player?) {
        val position = location.position3D
        if (MachineManager.hasMachine(position)) return
        
        for (machine in machineRegistry) {
            if (machine.structure.check(location, direction)) {
                MachineManager.activate(position, machine)
                
                player?.sendMessage("机器构建成功!")
                player?.playEffect(location, Effect.ENDER_SIGNAL, 1)
                player?.playEffect(location, Effect.CLICK1, 1)
                break
            }
        }
    }

    override fun getItemStack(): ItemStack = wrenchItem.getItemStack()
    
    fun getDictionaryItem(): IDictionaryItem = wrenchItem
    
}
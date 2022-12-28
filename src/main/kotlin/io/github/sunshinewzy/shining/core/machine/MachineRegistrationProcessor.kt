package io.github.sunshinewzy.shining.core.machine

import io.github.sunshinewzy.shining.api.machine.IMachine
import io.github.sunshinewzy.shining.api.machine.IMachineRegistrationProcessor
import io.github.sunshinewzy.shining.core.dictionary.DictionaryItem.Companion.dictionaryItem
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

object MachineRegistrationProcessor : IMachineRegistrationProcessor {
    
    
    
    override fun onRegister(machine: IMachine) {
        when(machine) {
            is SimpleMachine -> {
                
            }
            
            is PlaneMachine -> {
                
            }
            
            is MultiblockMachine -> {
                
            }
        }
    }


    @SubscribeEvent(EventPriority.HIGHEST)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        
    }
    
    @SubscribeEvent(EventPriority.HIGHEST)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val item = event.itemInHand.dictionaryItem
        
    }
    
    @SubscribeEvent(EventPriority.HIGHEST)
    fun onBlockBreak(event: BlockBreakEvent) {
        
    }
    
}
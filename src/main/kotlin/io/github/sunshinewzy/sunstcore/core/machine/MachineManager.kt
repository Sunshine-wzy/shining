package io.github.sunshinewzy.sunstcore.core.machine

import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

internal object MachineManager {
    
    @SubscribeEvent(EventPriority.HIGHEST)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if(event.hand != EquipmentSlot.HAND) return
        
        
    }
    
}
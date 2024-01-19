package io.github.sunshinewzy.shining.api.machine

import org.bukkit.event.player.PlayerInteractEvent

interface IMachineInteractContext : IMachineRunContext {
    
    val interactEvent: PlayerInteractEvent
    
}
package io.github.sunshinewzy.sunstcore.listeners

import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer

object PlayerListener {
    
    @SubscribeEvent
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.player.setupDataContainer()
    }
    
    @SubscribeEvent
    fun onPlayerQuit(e: PlayerQuitEvent) {
        e.player.releaseDataContainer()
    }
    
}
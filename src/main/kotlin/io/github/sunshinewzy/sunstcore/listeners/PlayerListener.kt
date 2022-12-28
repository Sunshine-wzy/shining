package io.github.sunshinewzy.sunstcore.listeners

import io.github.sunshinewzy.sunstcore.core.guide.SGuide
import io.github.sunshinewzy.sunstcore.utils.giveItem
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer

object PlayerListener {
    
    @SubscribeEvent
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        
        player.setupDataContainer()
        
        if(!player.hasPlayedBefore()) {
            player.giveItem(SGuide.getItem())
        }
    }
    
    @SubscribeEvent
    fun onPlayerQuit(e: PlayerQuitEvent) {
        e.player.releaseDataContainer()
    }
    
}
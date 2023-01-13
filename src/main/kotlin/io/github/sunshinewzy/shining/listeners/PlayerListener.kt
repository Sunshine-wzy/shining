package io.github.sunshinewzy.shining.listeners

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.data.database.player.PlayerDatabaseHandler.releaseDataContainer
import io.github.sunshinewzy.shining.core.data.database.player.PlayerDatabaseHandler.setupDataContainer
import io.github.sunshinewzy.shining.core.guide.GuideTeam.Companion.getGuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.utils.giveItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerListener {
    
    @SubscribeEvent
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        
        player.setupDataContainer()
        
        if(!player.hasPlayedBefore()) {
            player.giveItem(ShiningGuide.getItem())
        } else {
            Shining.scope.launch(Dispatchers.IO) { 
                player.getGuideTeam()?.let { team ->
                    if(player.uniqueId == team.captain) {
                        team.notifyCaptainApplication()
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    fun onPlayerQuit(e: PlayerQuitEvent) {
        e.player.releaseDataContainer()
    }
    
}
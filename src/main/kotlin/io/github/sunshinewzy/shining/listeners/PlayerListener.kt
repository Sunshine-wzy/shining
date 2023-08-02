package io.github.sunshinewzy.shining.listeners

import io.github.sunshinewzy.shining.core.data.database.player.PlayerDatabaseHandler.releaseDataContainer
import io.github.sunshinewzy.shining.core.data.database.player.PlayerDatabaseHandler.setupDataContainer
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam.Companion.letGuideTeam
import io.github.sunshinewzy.shining.utils.giveItem
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

object PlayerListener {

    @SubscribeEvent
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player

        player.setupDataContainer()

        if (!player.hasPlayedBefore()) {
            player.giveItem(ShiningGuide.getItem())
        } else {
            player.letGuideTeam { team ->
                if (player.uniqueId == team.captain) {
                    submit(delay = 20) {
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
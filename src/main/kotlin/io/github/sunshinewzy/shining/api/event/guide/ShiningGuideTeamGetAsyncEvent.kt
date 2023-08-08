package io.github.sunshinewzy.shining.api.event.guide

import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class ShiningGuideTeamGetAsyncEvent(
    val player: Player,
    var team: GuideTeam? = null
) : BukkitProxyEvent() {
    override val allowCancelled: Boolean
        get() = false
}
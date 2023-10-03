package io.github.sunshinewzy.shining.api.event.guide

import io.github.sunshinewzy.shining.api.event.ShiningEvent
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import org.bukkit.entity.Player

class ShiningGuideTeamGetAsyncEvent(
    val player: Player,
    var team: IGuideTeam? = null
) : ShiningEvent()
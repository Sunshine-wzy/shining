package io.github.sunshinewzy.shining.api.event.guide

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import org.bukkit.entity.Player

class ShiningGuideElementCompleteEvent(
    element: IGuideElement,
    val player: Player,
    val team: GuideTeam,
    val isSilent: Boolean
) : ShiningGuideElementEvent(element)
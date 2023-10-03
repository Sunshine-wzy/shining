package io.github.sunshinewzy.shining.api.event.guide

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import org.bukkit.entity.Player

class ShiningGuideElementUnlockEvent(
    element: IGuideElement,
    val player: Player,
    val team: IGuideTeam
) : ShiningGuideElementEvent(element)
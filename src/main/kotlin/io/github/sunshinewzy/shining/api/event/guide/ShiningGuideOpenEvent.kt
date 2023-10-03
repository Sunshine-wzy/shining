package io.github.sunshinewzy.shining.api.event.guide

import io.github.sunshinewzy.shining.api.event.ShiningEvent
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import org.bukkit.entity.Player

class ShiningGuideOpenEvent(
    val player: Player,
    val context: GuideContext,
    val isOpenLastElement: Boolean
) : ShiningEvent()
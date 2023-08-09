package io.github.sunshinewzy.shining.api.event.guide

import io.github.sunshinewzy.shining.api.guide.GuideContext
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class ShiningGuideOpenEvent(
    val player: Player,
    val context: GuideContext,
    val isOpenLastElement: Boolean
) : BukkitProxyEvent()
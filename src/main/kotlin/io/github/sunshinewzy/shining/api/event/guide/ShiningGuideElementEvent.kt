package io.github.sunshinewzy.shining.api.event.guide

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import taboolib.platform.type.BukkitProxyEvent

abstract class ShiningGuideElementEvent(val element: IGuideElement) : BukkitProxyEvent()
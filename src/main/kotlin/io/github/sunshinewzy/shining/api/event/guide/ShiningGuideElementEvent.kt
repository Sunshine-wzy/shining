package io.github.sunshinewzy.shining.api.event.guide

import io.github.sunshinewzy.shining.api.event.ShiningEvent
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement

abstract class ShiningGuideElementEvent(val element: IGuideElement) : ShiningEvent()
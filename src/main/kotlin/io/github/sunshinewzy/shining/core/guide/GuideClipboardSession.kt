package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer

class GuideClipboardSession(
    val element: IGuideElement,
    val container: IGuideElementContainer,
    val mode: Mode = Mode.CUT
) {
    
    enum class Mode {
        COPY, CUT
    }
    
}
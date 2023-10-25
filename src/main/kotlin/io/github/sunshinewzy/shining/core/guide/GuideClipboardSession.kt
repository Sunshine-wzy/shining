package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import java.util.function.Consumer

class GuideClipboardSession(
    val element: IGuideElement,
    val container: IGuideElementContainer?,
    val mode: Mode,
    val pasteCallback: Consumer<GuideClipboardSession> = Consumer {}
) {
    
    enum class Mode {
        COPY, CUT
    }
    
}
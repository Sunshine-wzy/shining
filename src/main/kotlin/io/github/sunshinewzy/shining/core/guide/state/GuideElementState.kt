package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState

abstract class GuideElementState(val element: IGuideElement) : IGuideElementState {
    
    
    
    override fun update(): Boolean =
        element.update(this)
    
    
}
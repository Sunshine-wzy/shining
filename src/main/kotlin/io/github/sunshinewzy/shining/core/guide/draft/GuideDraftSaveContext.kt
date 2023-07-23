package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement

class GuideDraftSaveContext(val state: IGuideElementState) : AbstractGuideContextElement(GuideDraftSaveContext) {
    
    companion object : GuideContext.Key<GuideDraftSaveContext>
    
}
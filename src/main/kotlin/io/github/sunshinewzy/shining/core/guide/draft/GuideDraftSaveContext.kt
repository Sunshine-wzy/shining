package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement

class GuideDraftSaveContext(
    val state: IGuideElementState,
    val team: GuideTeam,
    val context: GuideContext
) : AbstractGuideContextElement(GuideDraftSaveContext) {
    
    companion object : GuideContext.Key<GuideDraftSaveContext>
    
}
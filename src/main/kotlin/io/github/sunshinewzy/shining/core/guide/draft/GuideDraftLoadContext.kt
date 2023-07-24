package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement

class GuideDraftLoadContext(
    val team: GuideTeam,
    val element: IGuideElement?,
    val elementContainer: IGuideElementContainer?
) : AbstractGuideContextElement(GuideDraftLoadContext) {
    
    companion object : GuideContext.Key<GuideDraftLoadContext>
    
}
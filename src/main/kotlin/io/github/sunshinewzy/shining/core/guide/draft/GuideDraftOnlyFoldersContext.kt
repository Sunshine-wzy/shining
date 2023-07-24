package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement

class GuideDraftOnlyFoldersContext : AbstractGuideContextElement(GuideDraftOnlyFoldersContext) {
    
    companion object : GuideContext.Key<GuideDraftOnlyFoldersContext> {
        val INSTANCE: GuideDraftOnlyFoldersContext = GuideDraftOnlyFoldersContext()
    }

}
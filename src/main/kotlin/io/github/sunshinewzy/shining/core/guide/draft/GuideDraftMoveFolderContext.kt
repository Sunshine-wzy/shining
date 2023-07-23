package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.draft.IGuideDraft
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement

class GuideDraftMoveFolderContext(
    val draft: IGuideDraft,
    val previousFolder: GuideDraftFolder
) : AbstractGuideContextElement(GuideDraftMoveFolderContext) {
    
    companion object : GuideContext.Key<GuideDraftMoveFolderContext>
    
}
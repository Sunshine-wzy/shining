package io.github.sunshinewzy.shining.core.guide.context

import io.github.sunshinewzy.shining.api.guide.GuideContext

class GuideEditorContext : AbstractGuideContextElement(GuideEditorContext) {
    val mode: Boolean = false
    
    /**
     * Key for [GuideEditorContext] instance in the guide context.
     */
    companion object Key : GuideContext.Key<GuideEditorContext>
}
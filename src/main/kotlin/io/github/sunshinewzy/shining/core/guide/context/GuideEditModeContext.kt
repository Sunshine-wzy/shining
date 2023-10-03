package io.github.sunshinewzy.shining.core.guide.context

import io.github.sunshinewzy.shining.api.guide.context.GuideContext

class GuideEditModeContext(
    var mode: Boolean = true,
    var editor: Boolean = false
) : AbstractGuideContextElement(GuideEditModeContext) {
    
    fun isEditorEnabled(): Boolean = mode && editor
    
    /**
     * Key for [GuideEditModeContext] instance in the guide context.
     */
    companion object Key : GuideContext.Key<GuideEditModeContext>
}
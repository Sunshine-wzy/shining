package io.github.sunshinewzy.shining.core.guide.context

import io.github.sunshinewzy.shining.api.guide.GuideContext

class GuideEditorContext(
    var mode: Boolean = true,
    var editor: Boolean = false
) : AbstractGuideContextElement(GuideEditorContext) {
    
    fun isEditorEnabled(): Boolean = mode && editor
    
    /**
     * Key for [GuideEditorContext] instance in the guide context.
     */
    companion object Key : GuideContext.Key<GuideEditorContext>
}
package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement
import io.github.sunshinewzy.shining.core.guide.draft.GuideDraft
import taboolib.module.ui.type.Basic

class GuideElementStateEditorContext {
    
    class Back(val onBack: Basic.() -> Unit) : AbstractGuideContextElement(Back) {
        companion object : GuideContext.Key<Back>
    }
    
    class Builder(val builder: Basic.() -> Unit) : AbstractGuideContextElement(Builder) {
        companion object : GuideContext.Key<Builder>
    }
    
    class Save(val draft: GuideDraft) : AbstractGuideContextElement(Save) {
        companion object : GuideContext.Key<Save>
    }
    
}
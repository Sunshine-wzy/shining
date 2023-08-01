package io.github.sunshinewzy.shining.core.guide.context

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.core.guide.draft.GuideDraft
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.type.Basic

class GuideEditorContext {
    
    class Back(val onBack: ClickEvent.() -> Unit) : AbstractGuideContextElement(Back) {
        companion object : GuideContext.Key<Back>
    }
    
    class Builder(val builder: Basic.() -> Unit) : AbstractGuideContextElement(Builder) {
        companion object : GuideContext.Key<Builder>
    }
    
    class Update(val elementContainer: IGuideElementContainer) : AbstractGuideContextElement(Update) {
        companion object : GuideContext.Key<Update>
    }
    
    class Save(val draft: GuideDraft) : AbstractGuideContextElement(Save) {
        companion object : GuideContext.Key<Save>
    }
    
}
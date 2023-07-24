package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement
import taboolib.module.ui.type.Basic

class GuideElementStateEditorContext {
    
    class Back(val onBack: Basic.() -> Unit) : AbstractGuideContextElement(Back) {
        companion object : GuideContext.Key<Back>
    }
    
    class Builder(val builder: Basic.() -> Unit) : AbstractGuideContextElement(Builder) {
        companion object : GuideContext.Key<Builder>
    }
    
}
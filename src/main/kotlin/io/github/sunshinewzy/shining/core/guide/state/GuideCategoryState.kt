package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.core.guide.element.GuideCategory
import java.util.*

class GuideCategoryState(element: GuideCategory) : GuideElementState(element) {
    
    val elements: MutableList<IGuideElement> = LinkedList()
    
}
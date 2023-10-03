package io.github.sunshinewzy.shining.api.guide.team

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId

interface IGuideTeamData {

    fun getElementCondition(id: NamespacedId): ElementCondition?
    
    fun getElementCondition(element: IGuideElement): ElementCondition?

    fun setElementCondition(id: NamespacedId, condition: ElementCondition)
    
    fun setElementCondition(element: IGuideElement, condition: ElementCondition)
    
    fun removeElementCondition(id: NamespacedId): ElementCondition?
    
    fun removeElementCondition(element: IGuideElement): ElementCondition?

    fun getLastCompletedElement(): IGuideElement?

    fun setLastCompletedElement(element: IGuideElement)
    
    fun getElementRepeatablePeriod(id: NamespacedId): Long?
    
    fun setElementRepeatablePeriod(id: NamespacedId, period: Long)
    
}
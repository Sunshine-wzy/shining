package io.github.sunshinewzy.shining.core.guide.team

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry

class GuideTeamData {
    
    val elementConditionMap: MutableMap<NamespacedId, ElementCondition> = HashMap()
    var lastCompletedElementId: NamespacedId = NamespacedId.NULL
    val elementRepeatablePeriodMap: MutableMap<NamespacedId, Long> = HashMap()
    
    
    @JsonIgnore
    fun getElementCondition(element: IGuideElement): ElementCondition? =
        elementConditionMap[element.getId()]
    
    @JsonIgnore
    fun setElementCondition(element: IGuideElement, condition: ElementCondition) {
        elementConditionMap[element.getId()] = condition
    }

    @JsonIgnore
    fun getLastCompletedElement(): IGuideElement? {
        val id = lastCompletedElementId
        if (id == NamespacedId.NULL) return null
        return GuideElementRegistry.getElement(id)
    }
    
    @JsonIgnore
    fun setLastCompletedElement(element: IGuideElement) {
        lastCompletedElementId = element.getId()
    }
    
}
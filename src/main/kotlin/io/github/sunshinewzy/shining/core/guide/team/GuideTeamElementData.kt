package io.github.sunshinewzy.shining.core.guide.team

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId

class GuideTeamElementData {
    
    val elementConditionMap: MutableMap<NamespacedId, ElementCondition> = HashMap()
    
    
    @JsonIgnore
    fun getElementCondition(element: IGuideElement): ElementCondition? =
        elementConditionMap[element.getId()]
    
    @JsonIgnore
    fun setElementCondition(element: IGuideElement, condition: ElementCondition) {
        elementConditionMap[element.getId()] = condition
    }
    
}
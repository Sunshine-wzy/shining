package io.github.sunshinewzy.shining.core.guide.team

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeamData
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry

class GuideTeamData : IGuideTeamData {
    @JsonProperty
    private val elementConditionMap: MutableMap<NamespacedId, ElementCondition> = HashMap()
    @JsonProperty
    private var lastCompletedElementId: NamespacedId = NamespacedId.NULL
    @JsonProperty
    private val elementRepeatablePeriodMap: MutableMap<NamespacedId, Long> = HashMap()


    @JsonIgnore
    override fun getElementCondition(id: NamespacedId): ElementCondition? =
        elementConditionMap[id]

    @JsonIgnore
    override fun getElementCondition(element: IGuideElement): ElementCondition? =
        elementConditionMap[element.getId()]

    @JsonIgnore
    override fun setElementCondition(id: NamespacedId, condition: ElementCondition) {
        elementConditionMap[id] = condition
    }

    @JsonIgnore
    override fun setElementCondition(element: IGuideElement, condition: ElementCondition) {
        elementConditionMap[element.getId()] = condition
    }

    override fun removeElementCondition(id: NamespacedId): ElementCondition? =
        elementConditionMap.remove(id)

    override fun removeElementCondition(element: IGuideElement): ElementCondition? =
        elementConditionMap.remove(element.getId())

    @JsonIgnore
    override fun getLastCompletedElement(): IGuideElement? {
        val id = lastCompletedElementId
        if (id == NamespacedId.NULL) return null
        return GuideElementRegistry.getElement(id)
    }
    
    @JsonIgnore
    override fun setLastCompletedElement(element: IGuideElement) {
        lastCompletedElementId = element.getId()
    }

    @JsonIgnore
    override fun getElementRepeatablePeriod(id: NamespacedId): Long? =
        elementRepeatablePeriodMap[id]

    @JsonIgnore
    override fun setElementRepeatablePeriod(id: NamespacedId, period: Long) {
        elementRepeatablePeriodMap[id] = period
    }
    
}
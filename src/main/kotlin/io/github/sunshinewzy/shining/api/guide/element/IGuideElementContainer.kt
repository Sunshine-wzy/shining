package io.github.sunshinewzy.shining.api.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import java.util.concurrent.CompletableFuture

interface IGuideElementContainer : IGuideElement {

    fun registerElement(element: IGuideElement)
    
    fun unregisterElement(id: NamespacedId, cascade: Boolean)
    
    fun unregisterElement(element: IGuideElement, cascade: Boolean) {
        unregisterElement(element.getId(), cascade)
    }
    
    fun unregisterAllElements(cascade: Boolean)
    
    fun getElement(id: NamespacedId, isDeep: Boolean = false): IGuideElement?
    
    fun getElements(isDeep: Boolean = false): List<IGuideElement>
    
    fun getElementsByConditionFuture(team: IGuideTeam, condition: ElementCondition, isDeep: Boolean = false): CompletableFuture<List<IGuideElement>>

    fun updateElementId(element: IGuideElement, oldId: NamespacedId) {}

}
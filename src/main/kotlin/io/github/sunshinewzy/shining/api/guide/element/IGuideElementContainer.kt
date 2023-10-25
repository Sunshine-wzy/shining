package io.github.sunshinewzy.shining.api.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import java.util.concurrent.CompletableFuture

interface IGuideElementContainer : IGuideElement {

    fun registerElement(element: IGuideElement)
    
    fun unregisterElement(id: NamespacedId, cascade: Boolean, remove: Boolean)
    
    fun unregisterElement(id: NamespacedId, cascade: Boolean) {
        unregisterElement(id, cascade, true)
    }
    
    fun unregisterAllElements(cascade: Boolean, remove: Boolean)
    
    fun unregisterAllElements(cascade: Boolean) {
        unregisterAllElements(cascade, true)
    }
    
    fun getElement(id: NamespacedId, isDeep: Boolean = false): IGuideElement?
    
    fun getElements(isDeep: Boolean, container: Boolean): List<IGuideElement>
    
    fun getElements(isDeep: Boolean): List<IGuideElement> = getElements(isDeep, false)
    
    fun getElements(): List<IGuideElement> = getElements(false)
    
    fun getElementsByConditionFuture(team: IGuideTeam, condition: ElementCondition, isDeep: Boolean = false): CompletableFuture<List<IGuideElement>>

    fun updateElementId(element: IGuideElement, oldId: NamespacedId) {}

}
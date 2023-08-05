package io.github.sunshinewzy.shining.api.guide.element

import io.github.sunshinewzy.shining.api.namespace.NamespacedId

interface IGuideElementContainer : IGuideElement {

    fun registerElement(element: IGuideElement)
    
    fun unregisterElement(id: NamespacedId)
    
    fun unregisterElement(element: IGuideElement) {
        unregisterElement(element.getId())
    }
    
    fun getElements(): List<IGuideElement>

    fun updateElementId(element: IGuideElement, oldId: NamespacedId) {}

}
package io.github.sunshinewzy.shining.api.guide.state

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId

interface IGuideElementContainerState : IGuideElementState {
    
    fun addElement(id: NamespacedId)
    
    fun addElement(element: IGuideElement) {
        addElement(element.getId())
    }
    
    fun removeElement(id: NamespacedId): Boolean
    
    fun removeElement(element: IGuideElement): Boolean =
        removeElement(element.getId())
    
}
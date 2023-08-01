package io.github.sunshinewzy.shining.api.guide.state

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId

interface IGuideElementPriorityContainerState : IGuideElementContainerState {
    
    fun addElement(id: NamespacedId, priority: Int)
    
    fun addElement(element: IGuideElement, priority: Int) {
        addElement(element.getId(), priority)
    }

    override fun addElement(id: NamespacedId) {
        addElement(id, 0)
    }
    
}
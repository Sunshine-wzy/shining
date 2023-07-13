package io.github.sunshinewzy.shining.api.guide.element

interface IGuideElementPriorityContainer : IGuideElementContainer {

    fun registerElement(element: IGuideElement, priority: Int)
    
    
    override fun registerElement(element: IGuideElement) {
        registerElement(element, 0)
    }
    
}
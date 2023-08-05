package io.github.sunshinewzy.shining.api.guide.element

interface IGuideElementPriorityContainer : IGuideElementContainer {

    /**
     * @param priority Higher priority will make this [element] appear further up in the guide
     */
    fun registerElement(element: IGuideElement, priority: Int)

    override fun registerElement(element: IGuideElement) {
        registerElement(element, 0)
    }
    
}
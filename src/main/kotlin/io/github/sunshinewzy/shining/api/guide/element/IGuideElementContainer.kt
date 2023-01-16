package io.github.sunshinewzy.shining.api.guide.element

interface IGuideElementContainer : IGuideElement {

    fun registerElement(element: IGuideElement)
    
}
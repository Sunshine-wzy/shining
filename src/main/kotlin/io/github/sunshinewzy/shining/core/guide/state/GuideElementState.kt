package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.lock.ElementLock
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import java.util.*

abstract class GuideElementState(private var element: IGuideElement? = null) : IGuideElementState {
    
    var id: NamespacedId? = null
    var descriptionName: String? = null
    val descriptionLore: MutableList<String> = LinkedList()
    
    val dependencyMap: MutableMap<NamespacedId, IGuideElement> = HashMap()
    val locks: MutableList<ElementLock> = LinkedList()
    
    
    override fun update(): Boolean =
        element?.update(this) ?: false
    
    
}
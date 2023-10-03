package io.github.sunshinewzy.shining.core.guide.context

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GuideSelectElementsContext(
    val filter: (IGuideElement) -> Boolean = { true },
    private val callback: (GuideSelectElementsContext) -> Unit
) : AbstractGuideContextElement(GuideSelectElementsContext) {
    
    var mode: Boolean = false
        private set
    
    val elements = HashSet<IGuideElement>()
    
    
    fun getSelectorItem(player: Player): ItemStack =
        if (mode) itemSelector.toStateItem("open").shiny().toLocalizedItem(player)
        else itemSelector.toStateItem("close").toLocalizedItem(player)
    
    fun switchMode(): Boolean {
        mode = !mode
        return mode
    }
    
    fun submit() {
        callback(this)
    }
    
    
    companion object Key : GuideContext.Key<GuideSelectElementsContext> {
        val itemSelector = NamespacedIdItem(Material.ARROW, NamespacedId(Shining, "shining_guide-selector"))
    }
    
}
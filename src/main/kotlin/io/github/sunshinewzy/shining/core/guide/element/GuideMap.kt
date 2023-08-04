package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.state.GuideMapState
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.objects.Coordinate2D
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GuideMap : GuideElement, IGuideElementContainer {

    private var basePoint: Coordinate2D
    private val elements: MutableMap<Coordinate2D, IGuideElement> = HashMap()
    
    
    constructor(
        id: NamespacedId,
        description: ElementDescription,
        item: ItemStack,
        basePoint: Coordinate2D = Coordinate2D(2, 2)
    ) : super(id, description, item) {
        this.basePoint = basePoint
    }
    
    constructor() : super() {
        this.basePoint = Coordinate2D(2, 2)
    }


    override fun openMenu(player: Player, team: GuideTeam, context: GuideContext) {
        TODO("Not yet implemented")
    }
    
    override fun getState(): IGuideElementState =
        GuideMapState().correlateElement(this)

    override fun saveToState(state: IGuideElementState): Boolean {
        if (state !is GuideMapState) return false
        if (!super.saveToState(state)) return false
        
        state.elements.clear()
        state.setElementsByMap(elements)
        return true
    }

    override fun update(state: IGuideElementState, isMerge: Boolean): Boolean {
        if (state !is GuideMapState) return false
        if (!super<GuideElement>.update(state, isMerge)) return false
        
        if (!isMerge) {
            elements.clear()
        }
        state.getElementsMapTo(elements)
        return true
    }

    override fun register(): GuideMap {
        getElements().forEach { it.register() }
        return super.register() as GuideMap
    }

    fun registerElement(element: IGuideElement, coordinate: Coordinate2D) {
        elements[coordinate] = element
    }
    
    override fun registerElement(element: IGuideElement) {
        registerElement(element, Coordinate2D.ORIGIN)
    }

    override fun getElements(): List<IGuideElement> =
        elements.map { it.value }

}
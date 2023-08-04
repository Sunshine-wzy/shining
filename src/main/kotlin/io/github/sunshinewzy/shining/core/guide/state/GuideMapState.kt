package io.github.sunshinewzy.shining.core.guide.state

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.element.GuideMap
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.objects.Coordinate2D
import org.bukkit.entity.Player

class GuideMapState : GuideElementState() {

    var elements: MutableMap<Coordinate2D, NamespacedId> = HashMap()
    
    
    override fun toElement(): IGuideElement =
        GuideMap().also { it.update(this) }

    override fun clone(): GuideElementState {
        val state = GuideMapState()
        copyTo(state)
        
        state.elements += elements
        return state
    }

    override fun openAdvancedEditor(player: Player, team: GuideTeam, context: GuideContext) {
        TODO("Not yet implemented")
    }

    @JsonIgnore
    fun setElementsByMap(map: Map<Coordinate2D, IGuideElement>) {
        map.forEach { (coordinate, element) -> 
            elements[coordinate] = element.getId()
        }
    }
    
    @JsonIgnore
    fun getElementsMapTo(map: MutableMap<Coordinate2D, IGuideElement>): MutableMap<Coordinate2D, IGuideElement> {
        elements.forEach { (coordinate, id) -> 
            GuideElementRegistry.getElement(id)?.let { 
                map[coordinate] = it
            }
        }
        return map
    }
    
}
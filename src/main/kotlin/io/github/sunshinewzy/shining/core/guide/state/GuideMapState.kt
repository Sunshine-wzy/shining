package io.github.sunshinewzy.shining.core.guide.state

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementContainerState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.element.GuideMap
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.menu.MapMenu
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.onBuildEdge
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.objects.coordinate.Rectangle
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

class GuideMapState : GuideElementState(), IGuideElementContainerState {

    var basePoint: Coordinate2D = Coordinate2D(3, 3)
    var elements: MutableMap<Coordinate2D, NamespacedId> = HashMap()
    
    
    override fun toElement(): IGuideElement =
        GuideMap().also { it.update(this) }

    override fun clone(): GuideMapState {
        val state = GuideMapState()
        copyTo(state)
        
        state.elements += elements
        return state
    }

    fun addElement(id: NamespacedId, coordinate: Coordinate2D) {
        elements[coordinate] = id
    }
    
    override fun addElement(id: NamespacedId) {
        addElement(id, Coordinate2D.ORIGIN)
    }

    fun removeElement(coordinate: Coordinate2D) {
        elements -= coordinate
    }
    
    override fun removeElement(id: NamespacedId): Boolean = false

    override fun openAdvancedEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<MapMenu<IGuideElement>>(player.getLangText("menu-shining_guide-editor-state-map-title")) { 
            rows(6)
            area(Rectangle(2, 2, 8, 5))
            
            base(basePoint)
            elements { getElementsMap() }
            
            onGenerate(true) { player, element, _, _ -> 
                runBlocking(ShiningDispatchers.DB) { 
                    element.getUnlockedSymbol(player)
                }
            }

            onBuildEdge(GuideMap.edgeOrders)

            setMoveRight(9 orderWith 4) { ShiningIcon.MOVE_RIGHT.toLocalizedItem(player) }
            setMoveLeft(1 orderWith 4) { ShiningIcon.MOVE_LEFT.toLocalizedItem(player) }
            setMoveUp(2 orderWith 6) { ShiningIcon.MOVE_UP.toLocalizedItem(player) }
            setMoveDown(8 orderWith 6) { ShiningIcon.MOVE_DOWN.toLocalizedItem(player) }
            
            onClick { event, element, coordinate ->
                editElement(player, team, context, element, coordinate)
            }
            
            onClickEmpty { _, coordinate ->
                ShiningGuideEditor.openEditor(
                    player, team, GuideEditorContext.Back {
                        openAdvancedEditor(player, team, context)
                    } + ShiningGuideEditor.CreateContext {
                        addElement(it.getId(), coordinate)
                    }, null, null, this@GuideMapState
                )
            }
            
            onBack(player) { 
                openEditor(player, team, context)
            }
        }
    }

    fun editElement(player: Player, team: GuideTeam, context: GuideContext, element: IGuideElement, coordinate: Coordinate2D) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-map-element-title")) {
            rows(3)

            map(
                "-B-------",
                "-  a d  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK.toLocalizedItem(player)) {
                openAdvancedEditor(player, team, context)
            }
            
            // TODO
//            set('a', ) {
//                
//            }

            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                player.openDeleteConfirmMenu {
                    onConfirm { removeElement(coordinate) }
                    onFinal { openAdvancedEditor(player, team, context) }
                }
            }

            onClick(lock = true)
        }
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
    
    @JsonIgnore
    fun getElementsMap(): MutableMap<Coordinate2D, IGuideElement> = getElementsMapTo(HashMap())
    
}
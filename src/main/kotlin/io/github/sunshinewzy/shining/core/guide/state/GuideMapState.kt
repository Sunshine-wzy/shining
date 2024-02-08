package io.github.sunshinewzy.shining.core.guide.state

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSetter
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementContainerState
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.api.objects.coordinate.Rectangle
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.TextMap
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.element.GuideMap
import io.github.sunshinewzy.shining.core.guide.element.IGuideElementSuspend
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import io.github.sunshinewzy.shining.core.menu.MapMenu
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.onBuildEdge
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.orderWith
import io.github.sunshinewzy.shining.utils.toCurrentLocalizedItem
import kotlinx.coroutines.runBlocking
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest

class GuideMapState : GuideElementState(), IGuideElementContainerState {

    var basePoint: Coordinate2D = Coordinate2D(3, 3)
    var elements: MutableMap<Coordinate2D, NamespacedId> = HashMap()
    @JsonIgnore
    var idToCoordinate: MutableMap<NamespacedId, Coordinate2D> = HashMap()
    var removedElements: MutableSet<NamespacedId> = HashSet()
    
    
    override fun toElement(): IGuideElement =
        GuideMap().also { it.update(this) }

    override fun clone(): GuideMapState {
        val state = GuideMapState()
        copyTo(state)
        
        state.basePoint = basePoint
        state.elements += elements
        state.idToCoordinate += idToCoordinate
        state.removedElements += removedElements
        return state
    }

    fun addElement(id: NamespacedId, coordinate: Coordinate2D) {
        elements[coordinate] = id
        idToCoordinate[id] = coordinate
        removedElements -= id
    }
    
    override fun addElement(id: NamespacedId) {
        addElement(id, Coordinate2D.ORIGIN)
    }

    fun removeElement(coordinate: Coordinate2D): Boolean {
        elements.remove(coordinate)?.let { 
            idToCoordinate -= it
            removedElements += it
            return true
        }
        return false
    }
    
    override fun removeElement(id: NamespacedId): Boolean {
        val coordinate = idToCoordinate[id] ?: return false
        return removeElement(coordinate)
    }

    override fun openAdvancedEditor(player: Player, team: IGuideTeam, context: GuideContext) {
        player.openMenu<MapMenu<IGuideElement>>(player.getLangText("menu-shining_guide-editor-state-map-title")) { 
            rows(6)
            area(Rectangle(2, 2, 8, 5))
            
            base(basePoint)
            elements { getElementsMap() }

            context[GuideMap.OffsetContext]?.let {
                offset(it.offset)
            }
            
            onGenerate(true) { player, element, _, _ -> 
                runBlocking(ShiningDispatchers.DB) { 
                    (element as IGuideElementSuspend).getUnlockedSymbol(player)
                }
            }   

            onBuildEdge(GuideMap.edgeOrders)

            setMoveRight(9 orderWith 4) { ShiningIcon.MOVE_RIGHT.toLocalizedItem(player) }
            setMoveLeft(1 orderWith 4) { ShiningIcon.MOVE_LEFT.toLocalizedItem(player) }
            setMoveUp(2 orderWith 6) { ShiningIcon.MOVE_UP.toLocalizedItem(player) }
            setMoveDown(8 orderWith 6) { ShiningIcon.MOVE_DOWN.toLocalizedItem(player) }
            setMoveToOrigin(8 orderWith 1) { ShiningIcon.MOVE_TO_ORIGIN.toLocalizedItem(player) }
            
            onClick { event, element, coordinate ->
                editElement(player, team, context + GuideMap.OffsetContext(offset), element, coordinate)
            }
            
            onClickEmpty { _, coordinate ->
                ShiningGuideEditor.openEditor(
                    player, team, GuideEditorContext.Back {
                        openAdvancedEditor(player, team, context + GuideMap.OffsetContext(offset))
                    } + ShiningGuideEditor.CreateContext {
                        addElement(it.getId(), coordinate)
                    }, null, null, this@GuideMapState
                )
            }
            
            onBack(player) { 
                openEditor(player, team, context.minusKey(GuideMap.OffsetContext))
            }
            
            val theItemEditBasePoint = itemEditBasePoint.toCurrentLocalizedItem(player, "(${basePoint.x}, ${basePoint.y})")
            set(5 orderWith 1, theItemEditBasePoint) {
                player.openChatEditor<TextMap>(theItemEditBasePoint.getDisplayName()) {
                    map(mapOf(
                        "x" to basePoint.x.toString(),
                        "y" to basePoint.y.toString()
                    ))

                    predicate { it.toIntOrNull() != null }

                    onSubmit { content ->
                        val x = content["x"]?.toIntOrNull() ?: return@onSubmit
                        val y = content["y"]?.toIntOrNull() ?: return@onSubmit
                        basePoint = Coordinate2D(x, y)
                    }

                    onFinal { openAdvancedEditor(player, team, context + GuideMap.OffsetContext(offset)) }
                }
            }
        }
    }

    fun editElement(player: Player, team: IGuideTeam, context: GuideContext, element: IGuideElement, coordinate: Coordinate2D) {
        player.openMenu<Chest>(player.getLangText("menu-shining_guide-editor-state-map-element-title")) {
            rows(3)

            map(
                "-B-------",
                "- mun   -",
                "- lar d -",
                "- obp  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK.toLocalizedItem(player)) {
                openAdvancedEditor(player, team, context)
            }
            
            val theItemEditCoordinate = itemEditCoordinate.toCurrentLocalizedItem(player, "(${coordinate.x}, ${coordinate.y})")
            set('a', theItemEditCoordinate) {
                player.openChatEditor<TextMap>(theItemEditCoordinate.getDisplayName()) { 
                    map(mapOf(
                        "x" to coordinate.x.toString(),
                        "y" to coordinate.y.toString()
                    ))
                    
                    predicate { it.toIntOrNull() != null }
                    
                    onSubmit { content -> 
                        val x = content["x"]?.toIntOrNull() ?: return@onSubmit
                        val y = content["y"]?.toIntOrNull() ?: return@onSubmit
                        val newCoordinate = Coordinate2D(x, y)
                        
                        if (elements.containsKey(newCoordinate)) {
                            player.sendPrefixedLangText("text-shining_guide-editor-state-map-duplicate_coordinate")
                        } else {
                            removeElement(coordinate)
                            addElement(element.getId(), newCoordinate)
                        }
                        openAdvancedEditor(player, team, context)
                    }
                    
                    onCancel { editElement(player, team, context, element, coordinate) }
                }
            }

            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                player.openDeleteConfirmMenu {
                    onConfirm { removeElement(coordinate) }
                    onFinal { openAdvancedEditor(player, team, context) }
                }
            }
            
            fun move(offsetX: Int, offsetY: Int) {
                val newCoordinate = coordinate.add(offsetX, offsetY)
                if (elements.containsKey(newCoordinate)) {
                    player.sendPrefixedLangText("text-shining_guide-editor-state-map-duplicate_coordinate")
                } else {
                    removeElement(coordinate)
                    addElement(element.getId(), newCoordinate)
                }
                openAdvancedEditor(player, team, context)
            }
            
            set('u', ShiningIcon.MOVE_UP.toLocalizedItem(player)) {
                move(0, -1)
            }
            
            set('b', ShiningIcon.MOVE_DOWN.toLocalizedItem(player)) {
                move(0, 1)
            }
            
            set('l', ShiningIcon.MOVE_LEFT.toLocalizedItem(player)) {
                move(-1, 0)
            }
            
            set('r', ShiningIcon.MOVE_RIGHT.toLocalizedItem(player)) {
                move(1, 0)
            }
            
            set('m', ShiningIcon.MOVE_UP_LEFT.toLocalizedItem(player)) {
                move(-1, -1)
            }
            
            set('n', ShiningIcon.MOVE_UP_RIGHT.toLocalizedItem(player)) {
                move(1, -1)
            }
            
            set('o', ShiningIcon.MOVE_DOWN_LEFT.toLocalizedItem(player)) {
                move(-1, 1)
            }
            
            set('p', ShiningIcon.MOVE_DOWN_RIGHT.toLocalizedItem(player)) {
                move(1, 1)
            }

            onClick(lock = true)
        }
    }
    
    
    @JsonSetter("elements")
    fun setElementsAndIdToCoordinate(map: MutableMap<Coordinate2D, NamespacedId>) {
        elements.clear()
        idToCoordinate.clear()
        map.forEach { (coordinate, id) -> 
            elements[coordinate] = id
            idToCoordinate[id] = coordinate
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
    
    
    companion object {
        private val itemEditBasePoint = NamespacedIdItem(Material.COMPASS, NamespacedId(Shining, "shining_guide-editor-state-map-base_point"))
        private val itemEditCoordinate = NamespacedIdItem(Material.COMPASS, NamespacedId(Shining, "shining_guide-editor-state-map-coordinate"))
    }
    
}
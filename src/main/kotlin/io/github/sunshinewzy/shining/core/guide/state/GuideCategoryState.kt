package io.github.sunshinewzy.shining.core.guide.state

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSetter
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementPriorityContainerState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.element.GuideCategory
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.putSetElement
import io.github.sunshinewzy.shining.utils.toCurrentLocalizedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import java.util.*

class GuideCategoryState : GuideElementState(), IGuideElementPriorityContainerState {
    
    @get:JsonGetter("elements")
    @field:JsonIgnore
    var priorityToElements: TreeMap<Int, MutableSet<NamespacedId>> =
        TreeMap { o1, o2 -> o2 - o1 }
    @JsonIgnore
    var idToPriority: MutableMap<NamespacedId, Int> = HashMap()
    
    
    @JsonIgnore
    fun setPriorityToElementsByMap(map: TreeMap<Int, MutableSet<IGuideElement>>) {
        map.forEach { (priority, list) -> 
            priorityToElements[priority] = list.mapTo(HashSet()) { it.getId() }
        }
    }
    
    @JsonSetter("elements")
    fun setElementsById(map: TreeMap<Int, MutableSet<NamespacedId>>) {
        val newPriorityToElements = TreeMap<Int, MutableSet<NamespacedId>> { o1, o2 -> o2 - o1 }
        val newIdToPriority = HashMap<NamespacedId, Int>()

        map.forEach { (priority, list) ->
            list.forEach { id ->
                newIdToPriority[id] = priority
            }
            newPriorityToElements[priority] = list
        }

        priorityToElements = newPriorityToElements
        idToPriority = newIdToPriority
    }

    @JsonIgnore
    fun getElements(): List<IGuideElement> {
        val list = ArrayList<IGuideElement>()
        priorityToElements.forEach { (priority, ids) ->
            ids.mapNotNullTo(list) {
                GuideElementRegistry.getElement(it)
            }
        }
        return list
    }
    
    @JsonIgnore
    fun getPriorityToElementMapTo(map: MutableMap<Int, MutableSet<IGuideElement>>): MutableMap<Int, MutableSet<IGuideElement>> {
        if (map.isEmpty()) {
            priorityToElements.forEach { (priority, ids) ->
                val list = HashSet<IGuideElement>()
                ids.mapNotNullTo(list) {
                    GuideElementRegistry.getElement(it)
                }
                map[priority] = list
            }
        } else {
            val mergeMap = HashMap<IGuideElement, Int>()
            map.forEach { (priority, elementSet) -> 
                elementSet.forEach { 
                    mergeMap[it] = priority
                }
            }
            priorityToElements.forEach { (priority, ids) -> 
                ids.forEach { id ->
                    GuideElementRegistry.getElement(id)?.let { 
                        mergeMap[it] = priority
                    }
                }
            }
            map.clear()
            mergeMap.forEach { (theElement, priority) -> 
                map.putSetElement(priority, theElement)
            }
        }
        return map
    }
    
    @JsonIgnore
    fun getPriorityToElementMap(): Map<Int, MutableSet<IGuideElement>> =
        getPriorityToElementMapTo(HashMap())
    
    override fun addElement(id: NamespacedId, priority: Int) {
        priorityToElements.putSetElement(priority, id)
        idToPriority[id] = priority
    }
    
    override fun removeElement(id: NamespacedId): Boolean {
        val priority = idToPriority.remove(id) ?: return false
        return priorityToElements[priority]?.remove(id) ?: false
    }
    

    override fun toElement(): GuideCategory =
        GuideCategory().also { it.update(this) }

    override fun clone(): GuideCategoryState {
        val state = GuideCategoryState()
        copyTo(state)
        
        state.priorityToElements += priorityToElements
        state.idToPriority += idToPriority
        return state
    }

    override fun openAdvancedEditor(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMultiPageMenu<IGuideElement>(player.getLangText("menu-shining_guide-editor-state-category-title")) {
            elements { getElements() }
            
            onGenerate(true) { player, element, _, _ -> 
                element.getUnlockedSymbol(player)
            }
            
            onClick { _, element -> 
                editElement(player, team, context, element)
            }
            
            onClick(lock = true) { event ->
                if (ShiningGuide.isClickEmptySlot(event)) {
                    ShiningGuideEditor.openEditor(
                        player, team, GuideEditorContext.Back {
                            openAdvancedEditor(player, team, context)
                        }, null, null, this@GuideCategoryState
                    )
                }
            }
            
            onBack(player) { 
                openEditor(player, team, context)
            }
        }
    }
    
    fun editElement(player: Player, team: GuideTeam, context: GuideContext, element: IGuideElement) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-category-element-title")) { 
            rows(3)

            map(
                "-B-------",
                "-  a d  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                openAdvancedEditor(player, team, context)
            }

            val priority = idToPriority[element.getId()]
            set('a', itemEditPriority.toCurrentLocalizedItem(player, "&f$priority")) {
                player.openChatEditor<Text>(itemEditPriority.toLocalizedItem(player).getDisplayName()) { 
                    text(priority?.toString())
                    
                    predicate {
                        it.toIntOrNull() != null
                    }
                    
                    onSubmit { 
                        if (priority != null) {
                            removeElement(element)
                        }
                        addElement(element, it.toInt())
                    }
                    
                    onFinal { 
                        editElement(player, team, context, element)
                    }
                }
            }

            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                removeElement(element)
                openAdvancedEditor(player, team, context)
            }

            onClick(lock = true)
        }
    }
    
    
    companion object {
        private val itemEditPriority = NamespacedIdItem(Material.REPEATER, NamespacedId(Shining, "shining_guide-editor-state-category-priority"))
    }

}
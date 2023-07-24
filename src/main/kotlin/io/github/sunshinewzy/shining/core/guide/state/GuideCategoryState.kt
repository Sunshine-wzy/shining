package io.github.sunshinewzy.shining.core.guide.state

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSetter
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Text
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.element.GuideCategory
import io.github.sunshinewzy.shining.core.guide.element.GuideElements
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.putSetElement
import io.github.sunshinewzy.shining.utils.toCurrentLocalizedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isAir
import java.util.*

class GuideCategoryState : GuideElementState() {
    
    @JsonIgnore
    var priorityToElements: TreeMap<Int, MutableSet<IGuideElement>> =
        TreeMap { o1, o2 -> o2 - o1 }
    @JsonIgnore
    var idToPriority: MutableMap<NamespacedId, Int> = HashMap()
    
    
    @JsonGetter("elements")
    fun getElementsId(): TreeMap<Int, MutableList<NamespacedId>> {
        val map = TreeMap<Int, MutableList<NamespacedId>> { o1, o2 -> o2 - o1 }
        priorityToElements.forEach { (priority, list) -> 
            map[priority] = list.mapTo(ArrayList()) { it.getId() }
        }
        return map
    }
    
    @JsonSetter("elements")
    fun setElementsById(map: TreeMap<Int, MutableList<NamespacedId>>) {
        ShiningDispatchers.launchSQL {
            val newPriorityToElements = TreeMap<Int, MutableSet<IGuideElement>> { o1, o2 -> o2 - o1 }
            val newIdToPriority = HashMap<NamespacedId, Int>()
            
            map.forEach { (priority, list) ->
                val elements = HashSet<IGuideElement>()
                list.forEach { id ->
                    GuideElements.getElement(id)?.let {
                        newIdToPriority[id] = priority
                        elements += it
                    }
                }
                newPriorityToElements[priority] = elements
            }
            
            priorityToElements = newPriorityToElements
            idToPriority = newIdToPriority
        }
    }

    fun getElements(): List<IGuideElement> {
        val list = ArrayList<IGuideElement>()
        priorityToElements.forEach { (priority, elements) ->
            list += elements
        }
        return list
    }
    
    fun addElement(element: IGuideElement, priority: Int) {
        priorityToElements.putSetElement(priority, element)
        idToPriority[element.getId()] = priority
    }
    
    fun removeElement(element: IGuideElement): Boolean {
        val priority = idToPriority.remove(element.getId()) ?: return false
        return priorityToElements[priority]?.remove(element) ?: false
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

    override fun openAdvancedEditor(player: Player) {
        player.openMultiPageMenu<IGuideElement>(player.getLangText("menu-shining_guide-editor-state-category-title")) {
            elements { getElements() }
            
            onGenerate(true) { player, element, _, _ -> 
                element.getUnlockedSymbol(player)
            }
            
            onClick { _, element -> 
                editElement(player, element)
            }
            
            onClick(lock = true) { event ->
                if (event.rawSlot in ShiningGuide.slotOrders && event.currentItem.isAir()) {
                    
                }
            }
        }
    }
    
    fun editElement(player: Player, element: IGuideElement) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-state-category-element-title")) { 
            rows(3)

            map(
                "-B-------",
                "-  a d  -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                openAdvancedEditor(player)
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
                        editElement(player, element)
                    }
                }
            }

            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                removeElement(element)
                openAdvancedEditor(player)
            }

            onClick(lock = true)
        }
    }
    
    
    companion object {
        private val itemEditPriority = NamespacedIdItem(Material.REPEATER, NamespacedId(Shining, "shining_guide-editor-state-category-priority"))
    }

}
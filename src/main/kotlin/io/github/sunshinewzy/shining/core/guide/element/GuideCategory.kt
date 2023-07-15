package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementPriorityContainer
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor.setEditor
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.context.GuideSelectElementsContext
import io.github.sunshinewzy.shining.core.guide.context.GuideShortcutBarContext
import io.github.sunshinewzy.shining.core.guide.settings.ShiningGuideSettings
import io.github.sunshinewzy.shining.core.guide.state.GuideCategoryState
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import io.github.sunshinewzy.shining.utils.putElement
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import java.util.*

/**
 * Represent a category, which is showed in the guide.
 *
 * @param id to identify this [GuideCategory]
 * @param symbol to display this [GuideCategory] in guide
 */
open class GuideCategory : GuideElement, IGuideElementPriorityContainer {
    
    private val priorityToElements: TreeMap<Int, MutableList<IGuideElement>> =
        TreeMap { o1, o2 -> o2 - o1 }
    private val idToPriority: MutableMap<NamespacedId, Int> = HashMap()
    
    
    constructor(
        id: NamespacedId,
        description: ElementDescription,
        symbol: ItemStack
    ) : super(id, description, symbol)
    
    constructor() : super()
    

    override fun openMenu(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Linked<IGuideElement>>(player.getLangText(ShiningGuide.TITLE)) {
            rows(6)
            slots(ShiningGuide.slotOrders)

            elements { getElements() }

            val dependencyLockedElements = LinkedList<IGuideElement>()
            val lockLockedElements = LinkedList<IGuideElement>()
            onGenerate { player, element, index, slot ->
                if (context[GuideEditorContext]?.mode == true || team == GuideTeam.CompletedTeam) {
                    return@onGenerate element.getUnlockedSymbol(player)
                }
 
                val condition = element.getCondition(team)
                if (condition == ElementCondition.LOCKED_DEPENDENCY)
                    dependencyLockedElements += element
                else if (condition == ElementCondition.LOCKED_LOCK)
                    lockLockedElements += element
                element.getSymbolByCondition(player, team, condition)
            }

            onBuild(true, ShiningGuide.onBuildEdge)

            setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
                if (hasPreviousPage) {
                    ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.item
                } else ShiningIcon.EDGE.item
            }

            setNextPage(8 orderWith 6) { page, hasNextPage ->
                if (hasNextPage) {
                    ShiningIcon.PAGE_NEXT_GLASS_PANE.item
                } else ShiningIcon.EDGE.item
            }

            onClick { event, element ->
                if (context[GuideEditorContext]?.isEditorEnabled() == true) {
                    ShiningGuideEditor.openEditMenu(player, team, element, this@GuideCategory)
                    return@onClick
                }
                
                // Select elements
                context[GuideSelectElementsContext]?.let { ctxt ->
                    if (ctxt.mode) {
                        if (ctxt.elements.contains(element)) {
                            ctxt.elements.remove(element)
                        } else if (ctxt.filter(element)) {
                            ctxt.elements.add(element)
                        }

                        // Update shortcut bar
                        context[GuideShortcutBarContext]?.setItems(
                            ctxt.elements.map {
                                it.getUnlockedSymbol(player)
                            }
                        )
                        openMenu(player, team, context)
                        return@onClick
                    }
                }
                
                if (element in dependencyLockedElements) return@onClick

                if (element in lockLockedElements) {
                    if (element.unlock(player, team)) {
                        ShiningGuide.fireworkCongratulate(player)
                        open(player, team, null, context)
                    }
                    return@onClick
                }

                element.open(event.clicker, team, this@GuideCategory, context)
            }

            if (context[GuideEditorContext]?.isEditorEnabled() == true) {
                onClick(lock = true) {
                    if (ShiningGuide.isClickEmptySlot(it)) {
                        ShiningGuideEditor.openEditMenu(player, team, null, this@GuideCategory)
                    }
                }
            }

            setEditor(player, context) {
                openMenu(player, team, context)
            }

            if (this@GuideCategory !== ShiningGuide) {
                set(2 orderWith 1, ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player)) {
                    if (clickEvent().isShiftClick) {
                        ShiningGuide.openMainMenu(player, team, context)
                    } else {
                        back(player, team, context)
                    }
                }
            }

            if (team !== GuideTeam.CompletedTeam) {
                set(5 orderWith 1, ShiningIcon.SETTINGS.getLanguageItem().toLocalizedItem(player)) {
                    ShiningGuideSettings.openSettingsMenu(player, team)
                }
            }
            
            // Select elements
            context[GuideSelectElementsContext]?.let { ctxt ->
                set(4 orderWith 1, ctxt.getSelectorItem(player)) {
                    if (clickEvent().isShiftClick) {
                        ctxt.submit()
                    } else {
                        ctxt.switchMode()
                        openMenu(player, team, context)
                    }
                }
            }
            
            // Shortcut bar
            context[GuideShortcutBarContext]?.update(this)
        }
    }

    override fun saveToState(state: IGuideElementState): Boolean {
        if (state !is GuideCategoryState) return false
        if (!super.saveToState(state)) return false

        state.priorityToElements.clear()
        state.priorityToElements += priorityToElements
        state.idToPriority.clear()
        state.idToPriority += idToPriority
        return true
    }

    override fun getState(): GuideCategoryState =
        GuideCategoryState(this).also { saveToState(it) }

    override fun update(state: IGuideElementState): Boolean {
        if (state !is GuideCategoryState) return false
        if (!super.update(state)) return false

        priorityToElements.clear()
        priorityToElements += state.priorityToElements
        idToPriority.clear()
        idToPriority += state.idToPriority
        return true
    }

    override fun registerElement(element: IGuideElement, priority: Int) {
        priorityToElements.putElement(priority, element)
        idToPriority[element.getId()] = priority
    }
    
    
    fun getElements(): List<IGuideElement> {
        val list = ArrayList<IGuideElement>()
        priorityToElements.forEach { (priority, elements) -> 
            list += elements
        }
        return list
    }

}
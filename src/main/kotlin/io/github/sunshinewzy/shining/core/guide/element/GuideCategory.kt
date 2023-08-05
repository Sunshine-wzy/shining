package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementPriorityContainer
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor.setEditor
import io.github.sunshinewzy.shining.core.guide.context.GuideEditModeContext
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.context.GuideSelectElementsContext
import io.github.sunshinewzy.shining.core.guide.context.GuideShortcutBarContext
import io.github.sunshinewzy.shining.core.guide.settings.ShiningGuideSettings
import io.github.sunshinewzy.shining.core.guide.state.GuideCategoryState
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import io.github.sunshinewzy.shining.utils.putSetElement
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
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
    
    private val priorityToElements: TreeMap<Int, MutableSet<IGuideElement>> =
        TreeMap { o1, o2 -> o2 - o1 }
    private val idToPriority: MutableMap<NamespacedId, Int> = HashMap()
    private val removedElements: MutableSet<NamespacedId> = HashSet()
    
    
    constructor(id: NamespacedId, description: ElementDescription, symbol: ItemStack) : super(id, description, symbol)
    
    constructor() : super()
    

    override fun openMenu(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Linked<IGuideElement>>(player.getLangText(ShiningGuide.TITLE)) {
            rows(6)
            slots(ShiningGuide.slotOrders)

            elements { getElements() }

            val dependencyLockedElements = LinkedList<IGuideElement>()
            val lockLockedElements = LinkedList<IGuideElement>()
            onGenerate(true) { player, element, index, slot ->
                runBlocking(ShiningDispatchers.DB) {
                    if (context[GuideEditModeContext]?.mode == true || team == GuideTeam.CompletedTeam) {
                        return@runBlocking element.getUnlockedSymbol(player)
                    }

                    val condition = element.getCondition(team)
                    if (condition == ElementCondition.LOCKED_DEPENDENCY)
                        dependencyLockedElements += element
                    else if (condition == ElementCondition.LOCKED_LOCK)
                        lockLockedElements += element
                    element.getSymbolByCondition(player, team, condition)
                }
            }

            onBuild(false, ShiningGuide.onBuildEdge)

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
                if (context[GuideEditModeContext]?.isEditorEnabled() == true) {
                    ShiningGuideEditor.openEditor(
                        player, team, GuideEditorContext.Back {
                            openMenu(player, team, context)
                        } + ShiningGuideEditor.CreateContext {
                            registerElement(it)
                        }, element, this@GuideCategory
                    )
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
                        ShiningDispatchers.launchDB {
                            val list = ctxt.elements.map {
                                it.getUnlockedSymbol(player)
                            }
                            
                            submit {
                                context[GuideShortcutBarContext]?.setItems(list)
                                openMenu(player, team, context)
                            }
                        }
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

            if (context[GuideEditModeContext]?.isEditorEnabled() == true) {
                onClick(lock = true) {
                    if (ShiningGuide.isClickEmptySlot(it)) {
                        ShiningGuideEditor.openEditor(
                            player, team, GuideEditorContext.Back {
                                openMenu(player, team, context)
                            } + ShiningGuideEditor.CreateContext {
                                registerElement(it)
                            },null, this@GuideCategory
                        )
                    }
                }
            }

            setEditor(player, context) {
                openMenu(player, team, context)
            }

            if (this@GuideCategory !== ShiningGuide) {
                setBackButton(player, team, context)
            }

            set(5 orderWith 1, ShiningIcon.SETTINGS.getLanguageItem().toLocalizedItem(player)) {
                ShiningGuideSettings.openSettingsMenu(player, team)
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
        state.setPriorityToElementsByMap(priorityToElements)
        state.idToPriority.clear()
        state.idToPriority += idToPriority
        state.removedElements.clear()
        state.removedElements += removedElements
        return true
    }

    override fun getState(): IGuideElementState =
        GuideCategoryState().correlateElement(this)

    override fun update(state: IGuideElementState, isMerge: Boolean): Boolean {
        if (state !is GuideCategoryState) return false
        if (!super<GuideElement>.update(state, isMerge)) return false

        removedElements.clear()
        removedElements += state.removedElements
        removedElements.forEach { id ->
            if (idToPriority.contains(id)) {
                unregisterElement(id)
            }
        }
        
        if (!isMerge) {
            priorityToElements.clear()
            idToPriority.clear()
        }
        state.getPriorityToElementMapTo(priorityToElements)
        idToPriority += state.idToPriority
        return true
    }

    override fun registerElement(element: IGuideElement, priority: Int) {
        val id = element.getId()
        priorityToElements.putSetElement(priority, element)
        idToPriority[id] = priority
        removedElements -= id
    }

    override fun unregisterElement(id: NamespacedId) {
        val priority = idToPriority[id] ?: return
        priorityToElements[priority]?.let { elementSet ->
            for (element in elementSet) {
                if (element.getId() == id) {
                    idToPriority -= id
                    elementSet -= element
                    ShiningDispatchers.launchDB {
                        GuideElementRegistry.removeElement(element)
                    }
                    return
                }
            }
        }
    }

    override fun register(): GuideCategory {
        getElements().forEach { it.register() }
        return super.register() as GuideCategory
    }

    override fun getElements(): List<IGuideElement> {
        val list = ArrayList<IGuideElement>()
        priorityToElements.forEach { (_, elements) -> 
            list += elements
        }
        return list
    }
    
    override fun updateElementId(element: IGuideElement, oldId: NamespacedId) {
        idToPriority.remove(oldId)?.let { 
            val id = element.getId()
            idToPriority[id] = it
            removedElements -= id
        }
    }

}
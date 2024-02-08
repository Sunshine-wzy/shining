package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.ElementCondition.*
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.CompletedGuideTeam
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor.setEditor
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.context.GuideSelectElementsContext
import io.github.sunshinewzy.shining.core.guide.context.GuideShortcutBarContext
import io.github.sunshinewzy.shining.core.guide.settings.ShiningGuideSettings
import io.github.sunshinewzy.shining.core.guide.state.GuideCategoryState
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import io.github.sunshinewzy.shining.utils.putSetElement
import kotlinx.coroutines.runBlocking
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.PageableChest
import java.util.*

/**
 * Represent a category, which is showed in the guide.
 *
 * @param id to identify this [GuideCategory]
 * @param symbol to display this [GuideCategory] in guide
 */
open class GuideCategory : GuideElement, IGuideElementPriorityContainerSuspend {
    
    private val priorityToElements: TreeMap<Int, MutableSet<IGuideElement>> =
        TreeMap { o1, o2 -> o2 - o1 }
    private val idToPriority: MutableMap<NamespacedId, Int> = HashMap()
    private val removedElements: MutableSet<NamespacedId> = HashSet()
    
    
    constructor(id: NamespacedId, description: ElementDescription, symbol: ItemStack) : super(id, description, symbol)
    
    constructor() : super()
    

    override fun openMenu(player: Player, team: IGuideTeam, context: GuideContext) {
        ShiningDispatchers.launchDB { 
            val canComplete = canTeamComplete(team)

            submit {
                player.openMenu<PageableChest<IGuideElement>>(player.getLangText(ShiningGuide.TITLE)) {
                    rows(6)
                    slots(ShiningGuide.slotOrders)

                    elements { getElements() }

                    val dependencyLockedElements = HashSet<IGuideElement>()
                    val lockLockedElements = HashSet<IGuideElement>()
                    val repeatableElements = HashMap<IGuideElement, Long>()
                    onGenerate(true) { player, elementFuture, index, slot ->
                        val element = elementFuture as IGuideElementSuspend
                        runBlocking(ShiningDispatchers.DB) {
                            if (ShiningGuideEditor.isEditModeEnabled(player) || team == CompletedGuideTeam.getInstance()) {
                                return@runBlocking element.getUnlockedSymbol(player)
                            }

                            val condition = element.getCondition(team)
                            when (condition) {
                                LOCKED_DEPENDENCY -> dependencyLockedElements += element
                                LOCKED_LOCK -> lockLockedElements += element
                                REPEATABLE -> repeatableElements[element] = getTeamRepeatablePeriodRemainingTime(team)
                                else -> {}
                            }
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
                        if (ShiningGuideEditor.isEditModeAndEditorEnabled(player)) {
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
                                        (it as IGuideElementSuspend).getUnlockedSymbol(player)
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
                        
                        repeatableElements[element]?.let { remainingTime ->
                            if (remainingTime > 0) return@onClick
                        }

                        element.open(event.clicker, team, this@GuideCategory, context)
                    }

                    if (ShiningGuideEditor.isEditModeAndEditorEnabled(player)) {
                        onClick(lock = true) { event ->
                            if (ShiningGuide.isClickEmptySlot(event)) {
                                ShiningGuideEditor.openEditor(
                                    player, team, GuideEditorContext.Back {
                                        openMenu(player, team, context)
                                    } + ShiningGuideEditor.CreateContext {
                                        registerElement(it)
                                    },null, this@GuideCategory
                                )
                            }
                        }
                    } else if (this@GuideCategory !== ShiningGuide) {
                        if (canComplete) {
                            set(5 orderWith 6, ShiningIcon.VIEW_REWARDS_AND_SUBMIT.toLocalizedItem(player)) {
                                openViewRewardsMenu(player, team, context)
                                ShiningDispatchers.launchIO { 
                                    tryToComplete(player, team)
                                }
                            }
                        } else {
                            if (getRewards().isNotEmpty()) {
                                set(5 orderWith 6, ShiningIcon.VIEW_REWARDS.toLocalizedItem(player)) {
                                    openViewRewardsMenu(player, team, context)
                                }
                            }
                        }
                    }

                    setEditor(player) {
                        openMenu(player, team, context)
                    }

                    if (this@GuideCategory !== ShiningGuide) {
                        setBackButton(player, team, context)
                    }

                    set(5 orderWith 1, ShiningIcon.SETTINGS.getLanguageItem().toLocalizedItem(player)) {
                        ShiningGuideSettings.openSettingsMenu(player, team)
                        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
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
                    
                    onPageChange { it.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f) }
                }
            }
        }
    }

    override suspend fun checkComplete(player: Player, team: IGuideTeam): Boolean {
        return checkChildElementsCompleted(team)
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

    override fun update(state: IGuideElementState, merge: Boolean): Boolean {
        if (state !is GuideCategoryState) return false
        if (!super<GuideElement>.update(state, merge)) return false

        removedElements.clear()
        removedElements += state.removedElements
        removedElements.forEach { id ->
            if (idToPriority.contains(id)) {
                unregisterElement(id, true)
            }
        }
        
        if (!merge) {
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

    override fun unregisterElement(id: NamespacedId, cascade: Boolean, remove: Boolean) {
        val priority = idToPriority[id] ?: return
        priorityToElements[priority]?.let { elementSet ->
            for (element in elementSet) {
                if (element.getId() == id) {
                    if (cascade && element is IGuideElementContainer) {
                        element.unregisterAllElements(true, remove)
                    }
                    
                    idToPriority -= id
                    elementSet -= element
                    if (remove) {
                        ShiningDispatchers.transactionIO {
                            GuideElementRegistry.removeElement(element)
                        }
                    }
                    return
                }
            }
        }
    }

    override fun unregisterAllElements(cascade: Boolean, remove: Boolean) {
        priorityToElements.forEach { (_, elementSet) -> 
            elementSet.forEach { element ->
                if (cascade && element is IGuideElementContainer) {
                    element.unregisterAllElements(true, remove)
                }
                
                if (remove) {
                    ShiningDispatchers.transactionIO {
                        GuideElementRegistry.removeElement(element)
                    }
                }
            }
            elementSet.clear()
        }
        
        priorityToElements.clear()
        idToPriority.clear()
    }

    override fun register(): GuideCategory {
        getElements().forEach { it.register() }
        return super.register() as GuideCategory
    }

    override fun getElement(id: NamespacedId, isDeep: Boolean): IGuideElement? {
        idToPriority[id]?.let { priority ->
            priorityToElements[priority]?.let { elements ->
                elements.forEach { element ->
                    if (element.getId() == id) return element
                }
            }
        }
        
        if (isDeep) {
            priorityToElements.forEach { (priority, elements) -> 
                elements.forEach { element ->
                    if (element is IGuideElementContainer) {
                        element.getElement(id, true)?.let { 
                            return it
                        }
                    }
                }
            }
        }
        return null
    }

    override fun getElements(isDeep: Boolean, container: Boolean): List<IGuideElement> {
        val list = ArrayList<IGuideElement>()
        if (isDeep) {
            priorityToElements.forEach { (_, elements) ->
                elements.forEach { element ->
                    if (element is IGuideElementContainer) {
                        if (container) list += element
                        list += element.getElements(true)
                    } else list += element
                }
            }
        } else {
            priorityToElements.forEach { (_, elements) ->
                list += elements
            }
        }
        return list
    }

    override suspend fun getElementsByCondition(team: IGuideTeam, condition: ElementCondition, isDeep: Boolean): List<IGuideElement> {
        val list = ArrayList<IGuideElement>()
        if (isDeep) {
            priorityToElements.forEach { (_, elements) ->
                elements.forEach { elementFuture ->
                    val element = elementFuture as IGuideElementSuspend
                    val elementCondition = element.getCondition(team)
                    if (element is IGuideElementContainerSuspend) {
                        when (elementCondition) {
                            COMPLETE, UNLOCKED, REPEATABLE -> {
                                list += element.getElementsByCondition(team, condition, true)
                            }
                            LOCKED_DEPENDENCY, LOCKED_LOCK -> {
                                if (elementCondition == condition) {
                                    list += element
                                }
                            }
                        }
                    } else if (elementCondition == condition) {
                        list += element
                    }
                }
            }
        } else {
            priorityToElements.forEach { (_, elements) ->
                elements.forEach { element ->
                    if ((element as IGuideElementSuspend).getCondition(team) == condition) {
                        list += elements
                    }
                }
            }
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
    
    private suspend fun checkChildElementsCompleted(team: IGuideTeam): Boolean {
        priorityToElements.forEach { (_, elements) -> 
            elements.forEach { element ->
                if (!(element as IGuideElementSuspend).isTeamCompleted(team))
                    return false
            }
        }
        return true
    }

}
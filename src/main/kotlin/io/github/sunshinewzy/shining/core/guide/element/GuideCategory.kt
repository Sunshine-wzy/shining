package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor.setEditor
import io.github.sunshinewzy.shining.core.guide.ShiningGuideSettings
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.state.GuideCategoryState
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.isAir
import java.util.*

/**
 * Represent a category, which is showed in the guide.
 *
 * @param id to identify this [GuideCategory]
 * @param symbol to display this [GuideCategory] in guide
 */
open class GuideCategory(
    id: NamespacedId,
    description: ElementDescription,
    symbol: ItemStack
) : GuideElement(id, description, symbol), IGuideElementContainer {
    private val elements: MutableList<IGuideElement> = LinkedList()


    override fun openMenu(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Linked<IGuideElement>>(player.getLangText(ShiningGuide.TITLE)) {
            rows(6)
            slots(ShiningGuide.slotOrders)

            elements { elements }

            val dependencyLockedElements = LinkedList<IGuideElement>()
            val lockLockedElements = LinkedList<IGuideElement>()
            onGenerate { player, element, index, slot ->
                if (context[GuideEditorContext]?.mode == true) {
                    return@onGenerate element.getSymbolByCondition(player, team, ElementCondition.UNLOCKED)
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
                if (context[GuideEditorContext] != null) {
                    ShiningGuideEditor.openEditMenu(player, team, element, this@GuideCategory)
                    return@onClick
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

            if (context[GuideEditorContext] != null) {
                onClick(lock = true) {
                    if (it.rawSlot in ShiningGuide.slotOrders && it.currentItem.isAir()) {
                        ShiningGuideEditor.openEditMenu(player, team, null, this@GuideCategory)
                    }
                }
            }

            setEditor(player) {
                openMenu(player, team, context)
            }

            if (this@GuideCategory !== ShiningGuide) {
                set(2 orderWith 1, ShiningIcon.BACK_MENU.item) {
                    if (clickEvent().isShiftClick) {
                        ShiningGuide.openMainMenu(player, team, context)
                    } else {
                        back(player, team, context)
                    }
                }
            }

            if (team !== GuideTeam.CompletedTeam) {
                set(5 orderWith 1, ShiningIcon.SETTINGS.item) {
                    ShiningGuideSettings.openSettingsMenu(player, team)
                }
            }
        }
    }

    override fun saveToState(state: IGuideElementState): Boolean {
        if (state !is GuideCategoryState) return false
        if (!super.saveToState(state)) return false

        state.elements += elements
        return true
    }

    override fun getState(): GuideCategoryState =
        GuideCategoryState(this).also { saveToState(it) }

    override fun update(state: IGuideElementState): Boolean {
        if (state !is GuideCategoryState) return false
        if (!super.update(state)) return false

        elements.clear()
        elements += state.elements
        return true
    }

    override fun registerElement(element: IGuideElement) {
        elements += element
    }


}
package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.LanguageItem
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBackMenu
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import java.util.*

object ShiningGuideEditor {

    private val editModeMap: MutableMap<UUID, Boolean> = HashMap()

    private val itemEditor = NamespacedIdItem(Material.COMPARATOR, NamespacedId(Shining, "shining_guide-editor"))
    private val itemCreateStateCopy = NamespacedIdItem(Material.REDSTONE_LAMP, NamespacedId(Shining, "shining_guide-editor-create_state_copy"))
    private val itemCreateStateNew = NamespacedIdItem(Material.GLASS, NamespacedId(Shining, "shining_guide-editor-create_state_new"))

    const val PERMISSION_EDIT = "shining.guide.edit"


    @JvmOverloads
    fun openEditMenu(player: Player, team: GuideTeam, element: IGuideElement?, elementContainer: IGuideElementContainer? = null) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-editor-title")) {
            rows(6)

            map(
                "-B-------",
                "-ab     -",
                "-       -",
                "-       -",
                "-       -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            onBackMenu(player, team)

            if (element != null) {
                set('a', itemCreateStateCopy.toLocalizedItem(player)) {
                    val state = element.getState()
                    state.openEditor(player, team)
                }
            }

            set('b', itemCreateStateNew.toLocalizedItem(player)) {
                
            }

            onClick(lock = true)
        }
    }


    fun isEditModeEnabled(player: Player): Boolean =
        editModeMap.getOrDefault(player.uniqueId, false)

    fun switchEditMode(player: Player): Boolean =
        (!isEditModeEnabled(player)).also {
            editModeMap[player.uniqueId] = it
        }

    fun Basic.setEditor(
        player: Player,
        context: GuideContext,
        slot: Int = 6 orderWith 1,
        item: LanguageItem = itemEditor,
        onClick: ClickEvent.() -> Unit = {}
    ) {
        val editorContext = context[GuideEditorContext] ?: return
        if (!editorContext.mode) return
        
        set(
            slot,
            if (editorContext.editor) item.toStateItem("open").shiny().toLocalizedItem(player)
            else item.toStateItem("close").toLocalizedItem(player)
        ) {
            editorContext.editor = !editorContext.editor
            onClick(this)
        }
    }

}
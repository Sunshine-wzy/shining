package io.github.sunshinewzy.shining.core.guide.settings

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.ShiningGuideEditor
import io.github.sunshinewzy.shining.core.guide.draft.ShiningGuideDraft
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

object ShiningGuideSettings {

    private val itemTeamInfo = NamespacedIdItem(Material.APPLE, NamespacedId(Shining, "shining_guide-settings-team_info"))
    private val itemEditMode = NamespacedIdItem(Material.COMPARATOR, NamespacedId(Shining, "shining_guide-settings-edit_mode"))
    private val itemEditModeClose = itemEditMode.toStateItem("close")
    private val itemEditModeOpen = itemEditMode.toStateItem("open").shiny()
    private val itemDraftBox = NamespacedIdItem(Material.BOOKSHELF, NamespacedId(Shining, "shining_guide-settings-draft_box"))
    

    fun openSettingsMenu(player: Player, team: GuideTeam) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-settings-title")) {
            rows(6)

            map(
                "-B-------",
                "-a     e-",
                "-      f-",
                "-       -",
                "-       -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.getLanguageItem().toLocalizedItem(player), ShiningGuide.onClickBack)

            if (team !== GuideTeam.CompletedTeam) {
                set('a', itemTeamInfo.toLocalizedItem(player)) {
                    team.openInfoMenu(player)
                }
            }

            if (player.hasPermission(ShiningGuideEditor.PERMISSION_EDIT)) {
                set(
                    'e',
                    if (ShiningGuideEditor.isEditModeEnabled(player)) itemEditModeOpen.toLocalizedItem(player)
                    else itemEditModeClose.toLocalizedItem(player)
                ) {
                    ShiningGuideEditor.switchEditMode(player)
                    openSettingsMenu(player, team)
                }
                
                if (ShiningGuideEditor.isEditModeEnabled(player)) {
                    set('f', itemDraftBox.toLocalizedItem(player)) {
                        ShiningGuideDraft.openLastMenu(player)
                    }
                }
            }

            onClick(lock = true)
        }
    }

}
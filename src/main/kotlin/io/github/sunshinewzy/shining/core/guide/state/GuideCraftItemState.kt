package io.github.sunshinewzy.shining.core.guide.state

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.api.universal.item.UniversalItem
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.guide.element.GuideCraftItem
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.core.universal.item.UniversalItemRegistry
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest

class GuideCraftItemState : GuideElementState() {
    
    var craftItem: UniversalItem? = null
    var consume: Boolean = false
    

    override fun openAdvancedEditor(player: Player, team: IGuideTeam, context: GuideContext) {
        player.openMenu<Chest>(player.getLangText("menu-shining_guide-editor-state-craft_item-title")) {
            rows(3)
            
            map(
                "-B-------",
                "- a b c -",
                "---------"
            )
            
            onBack { openEditor(player, team, context) }
            onClick(lock = true)
            
            set('-', ShiningIcon.EDGE.item)
            set('a', ShiningIcon.CONSUME_MODE.toOpenOrCloseLocalizedItem(consume, player)) {
                consume = !consume
                openAdvancedEditor(player, team, context)
            }
            
            val theCraftItem = craftItem
            if (theCraftItem == null) {
                set('b', itemCreate.toLocalizedItem(player)) {
                    UniversalItemRegistry.openCreator(player, GuideEditorContext.Back {
                        openAdvancedEditor(player, team, context)
                    } + UniversalItemRegistry.CreateContext {
                        craftItem = it
                    })
                }
            } else {
                set('b', theCraftItem.getItemStack()) {
                    theCraftItem.openEditor(player, GuideEditorContext.Back {
                        openAdvancedEditor(player, team, context)
                    })
                }
            }
            
            set('c', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                player.openDeleteConfirmMenu { 
                    onConfirm { craftItem = null }
                    onFinal { openAdvancedEditor(player, team, context) }
                }
            }
        }
    }

    override fun toElement(): GuideCraftItem =
        GuideCraftItem().also { it.update(this) }

    override fun clone(): GuideCraftItemState {
        val state = GuideCraftItemState()
        copyTo(state)
        
        state.craftItem = craftItem?.clone()
        state.consume = consume
        return state
    }
    
    
    companion object {
        private val itemCreate = NamespacedIdItem(Material.GLASS_PANE, NamespacedId(Shining, "shining_guide-editor-state-craft_item-create"))
    }
    
}
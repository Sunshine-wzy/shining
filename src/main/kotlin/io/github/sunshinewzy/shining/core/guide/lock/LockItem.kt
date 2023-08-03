package io.github.sunshinewzy.shining.core.guide.lock

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.lock.ElementLock
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Item
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.containsItem
import io.github.sunshinewzy.shining.utils.getDisplayName
import io.github.sunshinewzy.shining.utils.getLoreOrNull
import io.github.sunshinewzy.shining.utils.removeSItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.colored
import taboolib.module.nms.getName
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem

class LockItem(
    item: ItemStack,
    isConsume: Boolean = true,
    @JsonIgnore private val itemArray: Array<ItemStack> = Array(1) { item }
) : ElementLock({ "${it.getLangText("menu-shining_guide-lock-item-description")} ${itemArray[0].getName(it)} x${itemArray[0].amount}" }, isConsume) {

    var item: ItemStack
        get() = itemArray[0]
        set(value) { itemArray[0] = value }
    
    
    override fun check(player: Player): Boolean =
        player.inventory.containsItem(item)

    override fun consume(player: Player) {
        player.inventory.removeSItem(item)
    }

    override fun openEditor(player: Player, team: GuideTeam, context: GuideContext, state: GuideElementState) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-lock-item-title").colored()) {
            rows(4)

            map(
                "-B-------",
                "- ca d  -",
                "-  i    -",
                "---------"
            )

            set('-', ShiningIcon.EDGE.item)

            set('B', ShiningIcon.BACK_MENU.toLocalizedItem(player)) {
                state.openLocksEditor(player, team, context)
            }

            set('c', if (isConsume) itemIsConsumeOpen.toLocalizedItem(player) else itemIsConsumeClose.toLocalizedItem(player)) {
                switchIsConsume()
                openEditor(player, team, context, state)
            }
            
            val theItemEditItem = itemEditItem.toLocalizedItem(player)
            set('a', theItemEditItem) {
                player.openChatEditor<Item>(theItemEditItem.getDisplayName()) { 
                    item(item)
                    
                    onSubmit { 
                        item = it
                    }
                    
                    onFinal { 
                        openEditor(player, team, context, state)
                    }
                }
            }
            
            set('i', item)
            
            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                state.locks -= this@LockItem
                state.openLocksEditor(player, team)
            }
            
            onClick(lock = true)
        }
    }

    override fun tip(player: Player) {
        player.openMenu<Basic>(player.getLangText(ShiningGuide.TITLE)) {
            rows(3)

            map(
                "#",
                "ooooa"
            )

            set('#', ShiningIcon.BACK_MENU.item, ShiningGuide.onClickBack)
            set('a', item)

            onClick(lock = true)
        }
    }

    override fun getIcon(player: Player): ItemStack =
        buildItem(Material.ITEM_FRAME) {
            name = player.getLangText("menu-shining_guide-lock-item-title")
            
            lore += description(player)
            lore += ""
            item.getLoreOrNull()?.forEach {
                lore += it
            }
            
            colored()
        }

    override fun clone(): LockItem =
        LockItem(item, isConsume)
    
    
    companion object {
        private val itemEditItem = NamespacedIdItem(Material.ITEM_FRAME, NamespacedId(Shining, "shining_guide-editor-lock-item"))
    }
    
}
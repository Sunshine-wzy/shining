package io.github.sunshinewzy.shining.core.item.universal

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.item.universal.UniversalItem
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.editor.chat.openChatEditor
import io.github.sunshinewzy.shining.core.editor.chat.type.Item
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

@JsonTypeName("vanilla")
class VanillaUniversalItem(var item: ItemStack) : UniversalItem {
    
    constructor() : this(ItemStack(Material.STONE))
    
    
    override fun getItemStack(): ItemStack = item.clone()

    override fun getItemAmount(): Int = item.amount

    override fun contains(inventory: Inventory): Boolean =
        inventory.containsItem(item)

    override fun contains(inventory: Inventory, amount: Int): Boolean {
        val singleItem = item.clone()
        singleItem.amount = 1
        return inventory.containsItem(singleItem, amount)
    }

    override fun consume(inventory: Inventory): Boolean =
        inventory.removeSItem(item)

    override fun openEditor(player: Player, context: GuideContext) {
        player.openMenu<Basic>(player.getLangText("menu-item-universal-vanilla-title")) { 
            rows(3)
            
            map(
                "-B===----",
                "-c=i=a b-",
                "--===----"
            )
            
            set('-', ShiningIcon.EDGE.item)
            set('=', ShiningIcon.EDGE_GLASS_PANE.item)
            set('c', itemCurrent.toLocalizedItem(player))
            set('i', item)
            
            set('a', itemHand.toLocalizedItem(player)) {
                player.openChatEditor<Item>(itemCurrent.getDisplayName()) { 
                    item(item)
                    
                    onSubmit { 
                        item = it.clone()
                    }
                    
                    onFinal { 
                        openEditor(player, context)
                    }
                }
            }
            
            set('b', itemEditor.toLocalizedItem(player)) {
                ItemEditor.editItem(item, player, GuideEditorContext.Back {
                    openEditor(player, context)
                })
            }
            
            context[GuideEditorContext.Back]?.let { 
                set('B', ShiningIcon.BACK.toLocalizedItem(player)) { it.onBack(this) }
            } ?: set('B', ShiningIcon.EDGE.item)
            
            onClick(lock = true)
        }
    }

    override fun clone(): VanillaUniversalItem = VanillaUniversalItem(item.clone())

    override fun isSimilar(other: UniversalItem, checkAmount: Boolean): Boolean {
        if (this === other) return true
        if (other !is VanillaUniversalItem) return false
        return item.isItemSimilar(other.item, checkAmount = checkAmount)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VanillaUniversalItem) return false
        return item.isItemSimilar(other.item)
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }


    companion object {
        val itemCurrent = NamespacedIdItem(Material.ITEM_FRAME, NamespacedId(Shining, "item-universal-vanilla-current"))
        private val itemHand = NamespacedIdItem(Material.EMERALD, NamespacedId(Shining, "item-universal-vanilla-hand"))
        private val itemEditor = NamespacedIdItem(Material.COMPARATOR, NamespacedId(Shining, "item-universal-vanilla-editor"))
    }
    
}
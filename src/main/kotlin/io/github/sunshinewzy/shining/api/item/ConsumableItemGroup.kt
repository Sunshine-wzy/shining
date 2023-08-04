package io.github.sunshinewzy.shining.api.item

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.item.universal.UniversalItem
import io.github.sunshinewzy.shining.api.item.universal.UniversalItemRegistry
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ConsumableItemGroup(
    var isConsume: Boolean,
    val items: MutableList<UniversalItem> = ArrayList()
): Cloneable {
    
    constructor() : this(true)
    
    constructor(isConsume: Boolean, item: UniversalItem) : this(isConsume, arrayListOf(item))
    
    
    @JsonIgnore
    fun getItemStacks(): MutableList<ItemStack> = items.mapTo(ArrayList()) { it.getItemStack() }
    
    fun contains(inventory: Inventory): Boolean {
        items.forEach { 
            if (!it.contains(inventory)) return false
        }
        return true
    }
    
    fun contains(player: Player): Boolean =
        contains(player.inventory)
    
    fun consume(inventory: Inventory): Boolean {
        items.forEach { 
            if (!it.consume(inventory)) return false
        }
        return true
    }
    
    fun consume(player: Player): Boolean =
        consume(player.inventory)
    
    fun openEditor(player: Player, context: GuideContext) {
        val (ctxt, ctxtRemove) = GuideEditorContext.Remove.getOrNew(context)

        player.openMultiPageMenu<UniversalItem>(player.getLangText("menu-item-consumable_item_group-title")) {
            elements { this@ConsumableItemGroup.items }

            onGenerate { _, element, _, _ -> element.getItemStack() }

            onClick { _, element ->
                if (ctxtRemove.mode) {
                    player.openDeleteConfirmMenu {
                        onConfirm { this@ConsumableItemGroup.items -= element }
                        onFinal { openEditor(player, ctxt) }
                    }
                } else {
                    element.openEditor(player, GuideEditorContext.Back {
                        openEditor(player, ctxt)
                    })
                }
            }

            ctxt[GuideEditorContext.Back]?.let {
                onBack { it.onBack(this) }
            }

            set(8 orderWith 1, ctxtRemove.getIcon(player)) {
                ctxtRemove.switchMode()
                openEditor(player, ctxt)
            }
            
            set(
                5 orderWith 1,
                if (isConsume) ShiningIcon.CONSUME_MODE.toStateShinyLocalizedItem("open", player)
                else ShiningIcon.CONSUME_MODE.toStateLocalizedItem("close", player)
            ) {
                isConsume = !isConsume
                openEditor(player, context)
            }
            
            onClick(lock = true) { event ->
                if (ShiningGuide.isClickEmptySlot(event)) {
                    UniversalItemRegistry.openCreator(player, GuideEditorContext.Back {
                        openEditor(player, context)
                    } + UniversalItemRegistry.CreateContext {
                        this@ConsumableItemGroup.items += it
                    })
                }
            }
        }
    }

    public override fun clone(): ConsumableItemGroup =
        ConsumableItemGroup(isConsume, items.mapTo(ArrayList()) { it.clone() })
    
}
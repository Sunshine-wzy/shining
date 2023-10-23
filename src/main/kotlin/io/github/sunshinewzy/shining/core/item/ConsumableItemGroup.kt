package io.github.sunshinewzy.shining.core.item

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.universal.item.UniversalItem
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.core.universal.item.UniversalItemRegistry
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

class ConsumableItemGroup(
    var isConsume: Boolean,
    val items: MutableList<UniversalItem> = ArrayList(),
    var checkMeta: Boolean = true,
    var checkName: Boolean = true,
    var checkLore: Boolean = true
): Cloneable {
    
    constructor() : this(true)
    
    constructor(
        isConsume: Boolean,
        item: UniversalItem,
        checkMeta: Boolean = true,
        checkName: Boolean = true,
        checkLore: Boolean = true
    ) : this(isConsume, arrayListOf(item), checkMeta, checkName, checkLore)
    
    
    @JsonIgnore
    fun getItemStacks(): MutableList<ItemStack> = items.mapTo(ArrayList()) { it.getItemStack() }
    
    @JsonIgnore
    fun getMergedMap(): MutableMap<UniversalItem, Int> {
        val map = TreeMap<UniversalItem, Int> { o1, o2 ->
            if (o1.isSimilar(o2, false, checkMeta = checkMeta, checkName = checkName, checkLore = checkLore)) 0 else 1
        }
        
        items.forEach { item ->
            map[item] = map.getOrDefault(item, 0) + item.getItemAmount()
        }
        return map
    }
    
    fun contains(inventory: Inventory): Boolean {
        getMergedMap().forEach { (item, amount) ->
            if (!item.contains(inventory, amount, checkMeta = checkMeta, checkName = checkName, checkLore = checkLore)) return false
        }
        return true
    }
    
    fun contains(player: Player): Boolean =
        contains(player.inventory)
    
    fun consume(inventory: Inventory): Boolean {
        items.forEach { 
            if (!it.consume(inventory, checkMeta = checkMeta, checkName = checkName, checkLore = checkLore)) return false
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
            
            set(4 orderWith 6, ShiningIcon.CHECK_META.toOpenOrCloseLocalizedItem(checkMeta, player)) {
                checkMeta = !checkMeta
                openEditor(player, context)
            }

            set(5 orderWith 6, ShiningIcon.CHECK_NAME.toOpenOrCloseLocalizedItem(checkName, player)) {
                checkName = !checkName
                openEditor(player, context)
            }

            set(6 orderWith 6, ShiningIcon.CHECK_LORE.toOpenOrCloseLocalizedItem(checkLore, player)) {
                checkLore = !checkLore
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
        ConsumableItemGroup(
            isConsume,
            items.mapTo(ArrayList()) { it.clone() },
            checkMeta = checkMeta,
            checkName = checkName,
            checkLore = checkLore
        )
    
}
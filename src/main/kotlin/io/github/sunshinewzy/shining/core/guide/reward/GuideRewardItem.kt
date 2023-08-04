package io.github.sunshinewzy.shining.core.guide.reward

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.reward.IGuideReward
import io.github.sunshinewzy.shining.api.item.universal.UniversalItem
import io.github.sunshinewzy.shining.api.item.universal.UniversalItemRegistry
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.context.GuideEditorContext
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.utils.giveItem
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

@JsonTypeName("item")
class GuideRewardItem(val items: MutableList<UniversalItem>) : IGuideReward {

    constructor() : this(LinkedList())
    
    
    override fun reward(player: Player) {
        items.forEach { 
            player.giveItem(it.getItemStack())
        }
    }

    override fun getIcon(player: Player): ItemStack = itemIcon.toLocalizedItem(player)

    override fun openEditor(player: Player, context: GuideContext) {
        val (ctxt, ctxtRemove) = GuideEditorContext.Remove.getOrNew(context)
        
        player.openMultiPageMenu<UniversalItem>(player.getLangText("menu-shining_guide-reward-item-title")) { 
            elements { this@GuideRewardItem.items }
            
            onGenerate { _, element, _, _ -> element.getItemStack() }
            
            onClick { _, element -> 
                if (ctxtRemove.mode) {
                    player.openDeleteConfirmMenu { 
                        onConfirm { this@GuideRewardItem.items -= element }
                        onFinal { openEditor(player, ctxt) }
                    }
                } else {
                    element.openEditor(player, GuideEditorContext.Back {
                        openEditor(player, ctxt)
                    })
                }
            }
            
            ctxt[GuideEditorContext.BackNoEvent]?.let {
                onBack { it.onBack() }
            }
            
            set(8 orderWith 1, ctxtRemove.getIcon(player)) {
                ctxtRemove.switchMode()
                openEditor(player, ctxt)
            }
            
            onClick(lock = true) { event ->
                if (ShiningGuide.isClickEmptySlot(event)) {
                    UniversalItemRegistry.openCreator(player, GuideEditorContext.Back {
                        openEditor(player, context)
                    } + UniversalItemRegistry.CreateContext {
                        this@GuideRewardItem.items += it
                    })
                }
            }
        }
    }

    override fun clone(): GuideRewardItem {
        return GuideRewardItem(ArrayList(items))
    }
    
    
    companion object {
        val itemIcon = NamespacedIdItem(Material.ITEM_FRAME, NamespacedId(Shining, "shining_guide-reward-item-icon"))
    }
    
}
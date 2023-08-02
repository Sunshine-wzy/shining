package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.item.ConsumableItemGroup
import io.github.sunshinewzy.shining.api.item.universal.UniversalItem
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.state.GuideItemState
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBuildEdge
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.OrderUtils
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked

open class GuideItem : GuideElement {
    
    private var itemGroup: ConsumableItemGroup = ConsumableItemGroup()
    
    
    constructor(id: NamespacedId, description: ElementDescription, item: ItemStack) : super(id, description, item)
    
    constructor() : super()
    

    override fun openMenu(player: Player, team: GuideTeam, context: GuideContext) {
        player.openMenu<Linked<UniversalItem>>(player.getLangText(ShiningGuide.TITLE)) { 
            rows(5)
            slots(slotOrders)
            
            elements { itemGroup.items }
            
            onGenerate { _, element, _, _ -> element.getItemStack() }
            
            onBuildEdge(edgeOrders)
            
            setPreviousPage(4 orderWith 5) { _, hasPreviousPage -> 
                if (hasPreviousPage) ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.toLocalizedItem(player)
                else ShiningIcon.EDGE.item
            }
            
            setNextPage(6 orderWith 5) { _, hasNextPage ->
                if (hasNextPage) ShiningIcon.PAGE_NEXT_GLASS_PANE.toLocalizedItem(player)
                else ShiningIcon.EDGE.item
            }
            
            setBackButton(player, team, context)
            
            set(2 orderWith 3, itemTip.toLocalizedItem(player))
            
            set(8 orderWith 3, ShiningIcon.SUBMIT.toLocalizedItem(player)) {
                TODO()
            }
            
//            set(5 orderWith 1)
        }
    }

    override fun saveToState(state: IGuideElementState): Boolean {
        if (state !is GuideItemState) return false
        if (!super.saveToState(state)) return false
        
        state.itemGroup = itemGroup
        return true
    }

    override fun getState(): IGuideElementState =
        GuideItemState().correlateElement(this)

    override fun update(state: IGuideElementState, isMerge: Boolean): Boolean {
        if (state !is GuideItemState) return false
        if (!super.update(state, isMerge)) return false
        
        state.itemGroup?.let { itemGroup = it }
        return true
    }

    override fun register(): GuideItem = super.register() as GuideItem
    
    
    @Suppress("ConvertArgumentToSet")
    companion object {
        val slotOrders: List<Int> = listOf(
            4 orderWith 2, 5 orderWith 2, 6 orderWith 2,
            4 orderWith 3, 5 orderWith 3, 6 orderWith 3,
            4 orderWith 4, 5 orderWith 4, 6 orderWith 4
        )
        
        val edgeOrders: List<Int> = OrderUtils.getFullOrders(5).also { 
            it -= slotOrders
        }
        
        private val itemTip = NamespacedIdItem(Material.PAPER, NamespacedId(Shining, "shining_guide-element-item-tip"))
    }
    
}
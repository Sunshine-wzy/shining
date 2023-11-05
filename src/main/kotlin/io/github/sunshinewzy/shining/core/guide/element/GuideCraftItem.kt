package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.api.universal.item.UniversalItem
import io.github.sunshinewzy.shining.api.universal.recipe.UniversalRecipe
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.state.GuideCraftItemState
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.menu.LinkedGroup
import io.github.sunshinewzy.shining.core.menu.onBuildEdge
import io.github.sunshinewzy.shining.core.universal.item.VanillaUniversalItem
import io.github.sunshinewzy.shining.utils.OrderUtils
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu

class GuideCraftItem : GuideElement {
    
    private var craftItem: UniversalItem
    
    
    constructor(
        id: NamespacedId,
        description: ElementDescription,
        symbol: ItemStack,
        craftItem: UniversalItem
    ) : super(id, description, symbol) {
        this.craftItem = craftItem
    }
    
    constructor() : super() {
        this.craftItem = VanillaUniversalItem()
    }
    

    override fun getState(): IGuideElementState =
        GuideCraftItemState().correlateElement(this)

    override fun saveToState(state: IGuideElementState): Boolean {
        if (state !is GuideCraftItemState) return false
        if (!super.saveToState(state)) return false
        
        state.craftItem = craftItem.clone()
        return true
    }

    override fun update(state: IGuideElementState, merge: Boolean): Boolean {
        if (state !is GuideCraftItemState) return false
        if (!super.update(state, merge)) return false
        
        state.craftItem?.let { craftItem = it.clone() }
        return true
    }

    override fun openMenu(player: Player, team: IGuideTeam, context: GuideContext) {
        player.openMenu<LinkedGroup<UniversalRecipe>>(player.getLangText(ShiningGuide.TITLE)) { 
            rows(5)
            slots(slotOrders)
            onBuildEdge(edgeOrders)
            
            
        }
    }

    override suspend fun checkComplete(player: Player, team: IGuideTeam): Boolean = false

    override fun register(): GuideCraftItem = super.register() as GuideCraftItem
    
    
    companion object {
        val slotOrders: List<Int> = listOf(
            4 orderWith 2, 5 orderWith 2, 6 orderWith 2,
            4 orderWith 3, 5 orderWith 3, 6 orderWith 3,
            4 orderWith 4, 5 orderWith 4, 6 orderWith 4
        )
        
        @Suppress("ConvertArgumentToSet")
        val edgeOrders: List<Int> = OrderUtils.getFullOrders(5).also { 
            it -= slotOrders
        }
    }
    
}
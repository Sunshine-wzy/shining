package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.api.universal.item.UniversalItem
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.state.GuideItemState
import io.github.sunshinewzy.shining.core.item.ConsumableItemGroup
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.onBuildEdge
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.OrderUtils
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import java.util.*

open class GuideItem : GuideElement {
    
    private var itemGroup: ConsumableItemGroup
    
    
    constructor(
        id: NamespacedId,
        description: ElementDescription,
        symbol: ItemStack,
        itemGroup: ConsumableItemGroup
    ) : super(id, description, symbol) {
        this.itemGroup = itemGroup
    }
    
    constructor() : super() {
        this.itemGroup = ConsumableItemGroup()
    }
    

    override fun openMenu(player: Player, team: IGuideTeam, context: GuideContext) {
        ShiningDispatchers.launchDB { 
            val canComplete = canTeamComplete(team)
            
            submit {
                player.openMenu<Linked<UniversalItem>>(player.getLangText(ShiningGuide.TITLE)) {
                    rows(5)
                    slots(slotOrders)

                    elements { itemGroup.items }

                    val playerInventory = player.inventory
                    val missingItems = ArrayList<ItemStack>()
                    val containedItems = TreeSet<UniversalItem> { o1, o2 ->
                        if (o1.isSimilar(o2, false, checkMeta = itemGroup.checkMeta, checkName = itemGroup.checkName, checkLore = itemGroup.checkLore)) 0 else 1
                    }
                    val mergedMap = itemGroup.getMergedMap()
                    mergedMap.forEach { (itemUniversal, itemAmount) -> 
                        if (itemUniversal.contains(playerInventory, itemAmount, checkMeta = itemGroup.checkMeta, checkName = itemGroup.checkName, checkLore = itemGroup.checkLore)) {
                            containedItems += itemUniversal
                        }
                    }
                    onGenerate { _, element, _, _ ->
                        if (element in containedItems)
                            buildItem(element.getItemStack()) { shiny() }
                        else element.getItemStack().also { missingItems += it }
                    }

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

                    if (canComplete) {
                        set(8 orderWith 3, ShiningIcon.SUBMIT.toLocalizedItem(player)) {
                            if (itemGroup.contains(player)) {
                                if (itemGroup.isConsume) itemGroup.consume(player)
                                complete(player, team)
                            } else {
                                openMissingItemsMenu(player, team, context, missingItems)
                            }
                        }
                    }

                    set(
                        5 orderWith 1,
                        if (itemGroup.isConsume) ShiningIcon.IS_CONSUME.toStateShinyLocalizedItem("open", player)
                        else ShiningIcon.IS_CONSUME.toStateLocalizedItem("close", player)
                    )
                    
                    if (getRewards().isNotEmpty()) {
                        set(5 orderWith 5, ShiningIcon.VIEW_REWARDS.toLocalizedItem(player)) {
                            openViewRewardsMenu(player, team, context)
                        }
                    }
                }
            }
        }
    }
    
    fun openMissingItemsMenu(player: Player, team: IGuideTeam, context: GuideContext, missingItems: List<ItemStack>) {
        player.openMenu<Linked<ItemStack>>(player.getLangText(ShiningGuide.TITLE)) {
            rows(5)
            slots(slotOrders)
            onBuildEdge(edgeOrders)
            
            elements { missingItems }
            onGenerate { _, element, _, _ -> element }

            setPreviousPage(4 orderWith 5) { _, hasPreviousPage ->
                if (hasPreviousPage) ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.toLocalizedItem(player)
                else ShiningIcon.EDGE.item
            }
            setNextPage(6 orderWith 5) { _, hasNextPage ->
                if (hasNextPage) ShiningIcon.PAGE_NEXT_GLASS_PANE.toLocalizedItem(player)
                else ShiningIcon.EDGE.item
            }
            
            onBack { 
                openMenu(player, team, context)
            }
            
            set(2 orderWith 3, itemTipMissingItems.toLocalizedItem(player))
            set(8 orderWith 3, ShiningIcon.SUBMIT_FAILURE.toLocalizedItem(player))
        }
    }

    override suspend fun checkComplete(player: Player, team: IGuideTeam): Boolean {
        return false
    }

    override fun getState(): IGuideElementState =
        GuideItemState().correlateElement(this)

    override fun saveToState(state: IGuideElementState): Boolean {
        if (state !is GuideItemState) return false
        if (!super.saveToState(state)) return false
        
        state.itemGroup = itemGroup.clone()
        return true
    }

    override fun update(state: IGuideElementState, merge: Boolean): Boolean {
        if (state !is GuideItemState) return false
        if (!super.update(state, merge)) return false
        
        state.itemGroup?.let { itemGroup = it.clone() }
        return true
    }

    override fun register(): GuideItem = super.register() as GuideItem
    
    
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
        
        private val itemTip = NamespacedIdItem(Material.PAPER, NamespacedId(Shining, "shining_guide-element-item-tip"))
        private val itemTipMissingItems = NamespacedIdItem(Material.PAPER, NamespacedId(Shining, "shining_guide-element-item-tip_missing_items"))
    }
    
}
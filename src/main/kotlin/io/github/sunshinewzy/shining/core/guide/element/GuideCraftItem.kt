package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.Shining
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
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.LinkedGroup
import io.github.sunshinewzy.shining.core.menu.onBuildEdge
import io.github.sunshinewzy.shining.core.universal.item.VanillaUniversalItem
import io.github.sunshinewzy.shining.core.universal.recipe.VanillaUniversalRecipe
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.OrderUtils
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.module.ui.openMenu

class GuideCraftItem : GuideElement {
    
    private var craftItem: UniversalItem
    private var consume: Boolean
    
    
    constructor(
        id: NamespacedId,
        description: ElementDescription,
        symbol: ItemStack,
        craftItem: UniversalItem,
        consume: Boolean
    ) : super(id, description, symbol) {
        this.craftItem = craftItem
        this.consume = consume
    }
    
    constructor() : super() {
        this.craftItem = VanillaUniversalItem()
        this.consume = false
    }
    

    override fun getState(): IGuideElementState =
        GuideCraftItemState().correlateElement(this)

    override fun saveToState(state: IGuideElementState): Boolean {
        if (state !is GuideCraftItemState) return false
        if (!super.saveToState(state)) return false
        
        state.craftItem = craftItem.clone()
        state.consume = consume
        return true
    }

    override fun update(state: IGuideElementState, merge: Boolean): Boolean {
        if (state !is GuideCraftItemState) return false
        if (!super.update(state, merge)) return false
        
        state.craftItem?.let { craftItem = it.clone() }
        consume = state.consume
        return true
    }

    override fun openMenu(player: Player, team: IGuideTeam, context: GuideContext) {
        val recipeCandidates = Bukkit.getRecipesFor(craftItem.getItemStack())
        val recipes = ArrayList<UniversalRecipe>()
        recipeCandidates.forEach { 
            if (!craftItem.isSimilar(it.result, checkAmount = false, checkMeta = true, checkName = true, checkLore = true)) return@forEach
            when (it) {
                is ShapedRecipe, is ShapelessRecipe -> {
                    recipes += VanillaUniversalRecipe(it)
                }
            }
        }
        if (recipes.isEmpty()) return
        
        ShiningDispatchers.launchDB { 
            val canComplete = canTeamComplete(team)
            
            submit {
                player.openMenu<LinkedGroup<UniversalRecipe>>(player.getLangText(ShiningGuide.TITLE)) {
                    rows(5)
                    slots(slotOrders)
                    onBuildEdge(edgeOrders)

                    elements { recipes }
                    onGenerate { _, element ->
                        val list = ArrayList<ItemStack>()
                        val recipe = element.getRecipe()
                        if (recipe.size == 9) {
                            recipe.mapTo(list) { it.getItemStack() }
                        }
                        list
                    }

                    setPreviousPage(3 orderWith 5) { _, hasPreviousPage ->
                        if (hasPreviousPage) ShiningIcon.PAGE_PREVIOUS_GLASS_PANE.toLocalizedItem(player)
                        else ShiningIcon.EDGE.item
                    }
                    setNextPage(7 orderWith 5) { _, hasNextPage ->
                        if (hasNextPage) ShiningIcon.PAGE_NEXT_GLASS_PANE.toLocalizedItem(player)
                        else ShiningIcon.EDGE.item
                    }

                    setBackButton(player, team, context)

                    set(2 orderWith 3, itemCraftingTable.toLocalizedItem(player))
                    set(8 orderWith 3, craftItem.getItemStack())
                    set(5 orderWith 1, ShiningIcon.IS_CONSUME.toOpenOrCloseLocalizedItem(consume, player))
                    if (getRewards().isNotEmpty()) {
                        set(8 orderWith 1, ShiningIcon.VIEW_REWARDS.toLocalizedItem(player)) {
                            openViewRewardsMenu(player, team, context)
                        }
                    }
                    
                    if (canComplete) {
                        set(5 orderWith 5, ShiningIcon.SUBMIT.toLocalizedItem(player)) {
                            if (craftItem.contains(player.inventory)) {
                                if (consume) craftItem.consume(player.inventory)
                                complete(player, team)
                            } else fail(player)
                        }
                    }
                    
                    var task: PlatformExecutor.PlatformTask? = null
                    onBuild { player, inventory ->
                        task?.cancel()
                        if (recipes[page].hasChoice()) {
                            val iterator = recipes[page].iterator()
                            task = submit(delay = 20L, period = 20L) task@{
                                if (!iterator.hasNext())
                                    iterator.reset()

                                val list = iterator.next()
                                if (list.size == 9) {
                                    list.forEachIndexed { index, itemStack ->
                                        inventory.setItem(slotOrders[index], itemStack)
                                    }
                                }
                                @Suppress("UnstableApiUsage")
                                player.updateInventory()
                            }
                        }
                    }
                    
                    onPageChange { task?.cancel() }
                    onClose { task?.cancel() }
                }
            }
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
        
        private val itemCraftingTable = NamespacedIdItem(Material.CRAFTING_TABLE, NamespacedId(Shining, "shining_guide-element-craft_item-crafting_table"))
    }
    
}
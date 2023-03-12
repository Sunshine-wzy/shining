package io.github.sunshinewzy.shining.core.task.tasks

import io.github.sunshinewzy.shining.core.task.TaskBase
import io.github.sunshinewzy.shining.core.task.TaskInventoryHolder
import io.github.sunshinewzy.shining.core.task.TaskProject.Companion.lastTaskProject
import io.github.sunshinewzy.shining.core.task.TaskStage
import io.github.sunshinewzy.shining.exceptions.NoRecipeException
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.SPageValue
import io.github.sunshinewzy.shining.objects.inventoryholder.SProtectInventoryHolder
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import java.util.*

class ItemCraftTask(
    taskStage: TaskStage,
    id: String,
    taskName: String,
    order: Int,
    predecessor: TaskBase?,
    symbol: ItemStack,
    reward: Array<ItemStack>,
    val craftItem: ItemStack,
    vararg descriptionLore: String,
) : TaskBase(taskStage, id, taskName, order, predecessor, symbol, reward, 5, *descriptionLore) {
    private var hasMultiPages = false
    private val pages = ArrayList<Array<ItemStack>>()


    init {
        isCreateEdge = false

        setSubmitItemOrder(9, 1)
        setBackItemOrder(9, 5)
        setSlotItem(6, 3, craftItem)
        setSlotItem(8, 3, ShiningIcon.WORKBENCH)

        for (i in 1..4) {
            setSlotItem(i, 1, whiteGlass)
            setSlotItem(i, 5, whiteGlass)
        }
        for (i in 2..4)
            setSlotItem(4, i, whiteGlass)


        val recipes = Bukkit.getServer().getRecipesFor(craftItem)
        if (recipes.isEmpty()) throw NoRecipeException(craftItem)

        val shapedRecipes = ArrayList<ShapedRecipe>()
        val shapelessRecipes = ArrayList<ShapelessRecipe>()
        recipes.forEach {
            if (!it.result.isItemSimilar(craftItem)) return@forEach

            when (it) {
                is ShapedRecipe ->
                    shapedRecipes.add(it)

                is ShapelessRecipe ->
                    shapelessRecipes.add(it)
            }
        }
        if (shapedRecipes.isEmpty() && shapelessRecipes.isEmpty())
            throw NoRecipeException(craftItem)

        if (shapedRecipes.size == 1 && shapelessRecipes.isEmpty()) {
            setCraftSlotItem(shapedRecipes.first().getRecipe())
        } else if (shapelessRecipes.size == 1 && shapedRecipes.isEmpty()) {
            setCraftSlotItem(shapelessRecipes.first().ingredientList)
        } else {
            shapedRecipes.forEach {
                pages.add(it.getRecipe())
            }
            shapelessRecipes.forEach { shapedRecipe ->
                val list = shapedRecipe.ingredientList
                val shapelessArray = Array(9) {
                    if (it in list.indices) {
                        list[it]
                    } else ItemStack(Material.AIR)
                }
                pages.add(shapelessArray)
            }

            hasMultiPages = true
            if (pages.isNotEmpty()) {
                setCraftSlotItem(pages.first())
                setSlotItem(nextPageOrder, ShiningIcon.PAGE_NEXT)
            }
            holder.page = 1
        }

    }


    override fun clickInventory(e: InventoryClickEvent) {
        val invHolder = e.inventory.holder as TaskInventoryHolder
        val player = e.view.asPlayer()

        when (e.rawSlot) {
            nextPageOrder ->
                if (hasMultiPages) {
                    val value = invHolder.page
                    val size = pages.size

                    if (value in 1..size) {
                        if (value < size) {
                            val inv = getTaskInv(player)
                            if (value == size - 1)
                                inv.setItem(nextPageOrder, whiteGlass)
                            inv.setItem(prePageOrder, ShiningIcon.PAGE_PREVIOUS.item)

                            inv.setCraftSlotItem(pages[value])
                            invHolder.page = value + 1
                            openTaskInv(player, inv)
                        }
                    } else invHolder.page = 1
                }

            prePageOrder ->
                if (hasMultiPages) {
                    val value = invHolder.page
                    val size = pages.size

                    if (value in 1..size) {
                        if (value > 1) {
                            val inv = getTaskInv(player)
                            if (value > 2)
                                inv.setItem(prePageOrder, ShiningIcon.PAGE_PREVIOUS.item)
                            inv.setItem(nextPageOrder, ShiningIcon.PAGE_NEXT.item)

                            inv.setCraftSlotItem(pages[value - 2])
                            invHolder.page = value - 1
                            openTaskInv(player, inv)
                        }
                    } else invHolder.page = 1
                }

            in craftSlotOrders -> {
                e.currentItem?.let {
                    if (it.type != Material.AIR)
                        it.openItemRecipeInv(player)
                }
            }
        }
    }

    override fun submit(player: Player) {
        if (player.inventory.containsItem(craftItem))
            completeTask(player)
        else requireNotEnough(player)
    }

    private fun setCraftSlotItem(craftOrder: Int, item: ItemStack) {
        setSlotItem(craftOrder.toX(3), 1 + craftOrder.toY(3), item)
    }

    private fun setCraftSlotItem(items: Array<ItemStack>) {
        items.forEachIndexed { i, itemStack ->
            setCraftSlotItem(i, itemStack)
        }
    }

    private fun setCraftSlotItem(items: List<ItemStack>) {
        items.forEachIndexed { i, itemStack ->
            setCraftSlotItem(i, itemStack)
        }
    }


    companion object DisplayItemRecipe {
        private const val holderName = "DisplayItemRecipe"
        private val whiteGlass = SItem(Material.WHITE_STAINED_GLASS_PANE, " ")
        private val nextPageOrder = 4 orderWith 5
        private val prePageOrder = 4 orderWith 1
        private val craftSlotOrders = arrayOf(9, 10, 11, 18, 19, 20, 27, 28, 29)

        init {
            subscribeEvent<InventoryClickEvent> {
                val player = view.asPlayer()
                val holder = inventory.holder
                if (holder !is SProtectInventoryHolder<*>) return@subscribeEvent

                val data = holder.data
                if (data == null || data !is Triple<*, *, *>) return@subscribeEvent

                val (first, second, third) = data
                if (first == null || first !is Pair<*, *> || second !is SPageValue || third !is ArrayList<*>) return@subscribeEvent

                val (pFirst, pSecond) = first
                if (pFirst !is String || pSecond !is UUID || pFirst != holderName || pSecond != player.uniqueId) return@subscribeEvent

                val value = second.page
                val pages = third.castList<Array<ItemStack>>()
                val inv = inventory

                when (rawSlot) {
                    nextPageOrder -> {
                        val size = pages.size

                        if (value in 1..size) {
                            if (value < size) {
                                if (value == size - 1)
                                    inv.setItem(nextPageOrder, whiteGlass)
                                inv.setItem(prePageOrder, ShiningIcon.PAGE_PREVIOUS.item)

                                inv.setCraftSlotItem(pages[value])

                                second.page = value + 1
                            }
                        } else second.page = 1
                    }

                    prePageOrder -> {
                        val size = pages.size

                        if (value in 1..size) {
                            if (value > 1) {
                                if (value > 2)
                                    inv.setItem(prePageOrder, ShiningIcon.PAGE_PREVIOUS.item)
                                inv.setItem(nextPageOrder, ShiningIcon.PAGE_NEXT.item)

                                inv.setCraftSlotItem(pages[value - 2])
                                second.page = value - 1
                            }
                        } else second.page = 1
                    }

                    9 orderWith 5 -> {
                        val uuid = player.uniqueId
                        if (lastTaskProject.containsKey(uuid)) {
                            lastTaskProject[uuid]?.let { project ->
                                if (project.lastTaskInv.containsKey(uuid)) {
                                    project.lastTaskInv[uuid]?.let {
                                        it.openTaskInv(player)
                                        return@subscribeEvent
                                    }
                                }
                            }
                        }
                        player.closeInventory()
                        return@subscribeEvent
                    }

                    in craftSlotOrders -> {
                        currentItem?.let {
                            if (it.type != Material.AIR)
                                it.openItemRecipeInv(player)
                        }
                        return@subscribeEvent
                    }

                    else -> return@subscribeEvent
                }

                player.playSound(player.location, Sound.ENTITY_HORSE_ARMOR, 1f, 1.2f)
                player.updateInventory()
            }
        }


        fun ItemStack.openItemRecipeInv(player: Player) {
            val pages = ArrayList<Array<ItemStack>>()
            val holder = SProtectInventoryHolder(
                Triple(holderName to player.uniqueId, SPageValue(1), pages)
            )
            val inv = Bukkit.createInventory(holder, 5 * 9, "合成表")
            inv.setItem(9, 5, ShiningIcon.BACK_MENU)

            val recipes = Bukkit.getServer().getRecipesFor(this)
            if (recipes.isEmpty()) {
                inv.setItem(5, 3, SItem(Material.STRUCTURE_VOID, "§c这个物品没有合成表！"))
                player.openInventoryWithSound(inv)
                return
            }

            val shapedRecipes = ArrayList<ShapedRecipe>()
            val shapelessRecipes = ArrayList<ShapelessRecipe>()
            recipes.forEach {
                if (!it.result.isItemSimilar(this)) return@forEach

                when (it) {
                    is ShapedRecipe ->
                        shapedRecipes.add(it)

                    is ShapelessRecipe ->
                        shapelessRecipes.add(it)
                }
            }
            if (shapedRecipes.isEmpty() && shapelessRecipes.isEmpty()) {
                inv.setItem(5, 3, SItem(Material.STRUCTURE_VOID, "§c这个物品没有合成表！"))
                player.openInventoryWithSound(inv)
                return
            }

            inv.setItem(6, 3, this)
            inv.setItem(8, 3, ShiningIcon.WORKBENCH)

            for (i in 1..4) {
                inv.setItem(i, 1, whiteGlass)
                inv.setItem(i, 5, whiteGlass)
            }
            for (i in 2..4)
                inv.setItem(4, i, whiteGlass)


            if (shapedRecipes.size == 1 && shapelessRecipes.isEmpty()) {
                inv.setCraftSlotItem(shapedRecipes.first().getRecipe())
            } else if (shapelessRecipes.size == 1 && shapedRecipes.isEmpty()) {
                inv.setCraftSlotItem(shapelessRecipes.first().ingredientList)
            } else {
                shapedRecipes.forEach {
                    pages.add(it.getRecipe())
                }
                shapelessRecipes.forEach { shapedRecipe ->
                    val list = shapedRecipe.ingredientList
                    val shapelessArray = Array(9) {
                        if (it in list.indices) {
                            list[it]
                        } else ItemStack(Material.AIR)
                    }
                    pages.add(shapelessArray)
                }

                if (pages.isNotEmpty()) {
                    inv.setCraftSlotItem(pages.first())
                    inv.setItem(nextPageOrder, ShiningIcon.PAGE_NEXT)
                }
            }

            player.openInventoryWithSound(inv)
        }

    }

}
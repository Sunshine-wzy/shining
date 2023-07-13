package io.github.sunshinewzy.shining.objects

import io.github.sunshinewzy.shining.utils.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.java.JavaPlugin

open class SItem(item: ItemStack) : ItemStack(item) {

    constructor(item: ItemStack, amount: Int) : this(item) {
        this.amount = amount
    }

    constructor(item: ItemStack, name: String) : this(item) {
        setName(name)
    }

    constructor(item: ItemStack, name: String, vararg lore: String) : this(item) {
        setNameAndLore(name, lore.toList())
    }

    constructor(item: ItemStack, lore: List<String>) : this(item) {
        setLore(lore)
    }

    constructor(type: Material) : this(ItemStack(type))
    constructor(type: Material, name: String) : this(type) {
        setName(name)
    }

    constructor(type: Material, name: String, vararg lore: String) : this(type) {
        setNameAndLore(name, lore.toList())
    }

    constructor(type: Material, name: String, lore: List<String>) : this(type) {
        setNameAndLore(name, lore)
    }

    constructor(type: Material, lore: List<String>) : this(type) {
        setLore(lore)
    }

    constructor(type: Material, amount: Int) : this(ItemStack(type, amount))
    constructor(type: Material, amount: Int, name: String) : this(type, amount) {
        setName(name)
    }

    constructor(type: Material, amount: Int, name: String, vararg lore: String) : this(type, amount) {
        setNameAndLore(name, lore.toList())
    }

    constructor(type: Material, damage: Short, amount: Int) : this(ItemStack(type, amount, damage))
    constructor(type: Material, damage: Short, amount: Int, name: String) : this(type, damage, amount) {
        setName(name)
    }

    constructor(type: Material, damage: Short, amount: Int, name: String, vararg lore: String) : this(
        type,
        damage,
        amount
    ) {
        setNameAndLore(name, lore.toList())
    }

    constructor(type: Material, damage: Short, amount: Int, name: String, lore: List<String>) : this(
        type,
        damage,
        amount
    ) {
        setNameAndLore(name, lore)
    }


    /**
     * 添加物品行为 - 当玩家手持物品交互时调用添加的行为
     * 此函数已经帮您判断好了 [PlayerInteractEvent] 事件的物品(确保为你的 [SItem])
     * 无需重复判断
     */
    fun addAction(filter: PlayerInteractEvent.() -> Boolean = { true }, block: PlayerInteractEvent.() -> Unit): SItem {
        val actions = itemActions[this] ?: kotlin.run {
            itemActions[this] = filter to arrayListOf(block)
            return this
        }

        actions.second.add(block)
        return this
    }

    fun addAction(block: PlayerInteractEvent.() -> Unit): SItem {
        addAction({ true }, block)
        return this
    }


    override fun equals(other: Any?): Boolean =
        when {
            other == null -> false
            this === other -> true
            other !is ItemStack -> false
            else -> isItemSimilar(other)
        }

    override fun hashCode(): Int {
        var hash = 1
        hash = hash * 31 + type.hashCode()
        hash = hash * 31 + amount
        hash = hash * 31 + if (hasItemMeta()) itemMeta.hashCode() else 0
        return hash
    }

    companion object {
        private val itemActions = HashMap<SItem, Pair<PlayerInteractEvent.() -> Boolean, ArrayList<PlayerInteractEvent.() -> Unit>>>()

        val AIR = ItemStack(Material.AIR)
        val items = HashMap<String, ItemStack>()

        internal fun initAction() {
            subscribeEvent<PlayerInteractEvent> {
                val item = item
                if (item == null || item.type == Material.AIR) return@subscribeEvent

                itemActions.forEach { (sItem, pair) ->
                    if (pair.first(this) && item.isItemSimilar(sItem)) {
                        pair.second.forEach { it(this) }
                    }
                }
            }
        }


        fun ItemStack.addRecipe(plugin: JavaPlugin, recipe: Recipe): ItemStack {
            plugin.server.addRecipe(recipe)
            return this
        }

        fun ItemStack.addRecipe(plugin: JavaPlugin, vararg recipes: Recipe): ItemStack {
            recipes.forEach {
                plugin.server.addRecipe(it)
            }

            return this
        }

        fun ItemStack.addShapedRecipe(
            plugin: JavaPlugin,
            key: String,
            ingredient: Map<Char, Material>,
            line1: String = "",
            line2: String = "",
            line3: String = ""
        ): ItemStack {
            addRecipe(
                plugin,
                SShapedRecipe(
                    NamespacedKey(plugin, key),
                    this,
                    ingredient,
                    line1,
                    line2,
                    line3
                )
            )
            return this
        }

        fun ItemStack.addShapedRecipeByChoice(
            plugin: JavaPlugin,
            key: String,
            ingredient: Map<Char, SRecipeChoice>,
            line1: String = "",
            line2: String = "",
            line3: String = ""
        ): ItemStack {
            addRecipe(
                plugin, SShapedRecipe.byChoice(
                    plugin, key, this, ingredient, line1, line2, line3
                )
            )

            return this
        }

        fun ItemStack.addShapelessRecipe(
            plugin: JavaPlugin,
            key: String,
            ingredients: List<Pair<Int, Material>>
        ): ItemStack {
            val recipe = ShapelessRecipe(
                NamespacedKey(plugin, key),
                this
            )
            ingredients.forEach {
                recipe.addIngredient(it.first, it.second)
            }

            addRecipe(plugin, recipe)
            return this
        }

        fun ItemStack.addShapelessRecipe(
            plugin: JavaPlugin,
            key: String,
            count: Int,
            ingredient: Material
        ): ItemStack {
            addRecipe(
                plugin,
                ShapelessRecipe(
                    NamespacedKey(plugin, key),
                    this
                ).addIngredient(count, ingredient)
            )
            return this
        }

    }
}
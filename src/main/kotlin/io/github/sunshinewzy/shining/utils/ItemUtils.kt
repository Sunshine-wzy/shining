package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.api.Itemable
import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import io.github.sunshinewzy.shining.core.lang.formatArgs
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.lang.node.ListNode
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.core.lang.node.TextNode
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.module.chat.colored
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem
import java.util.*
import java.util.concurrent.ThreadLocalRandom

fun buildItem(item: Itemable, builder: ItemBuilder.() -> Unit = {}): ItemStack {
    return buildItem(item.getItemStack(), builder)
}

fun ItemStack.localize(languageNode: LanguageNode?): ItemStack {
    when (languageNode) {
        is TextNode -> {
            setName(languageNode.text)
        }

        is ListNode -> {
            val list = LinkedList<String>()
            languageNode.list.filterIsInstance<TextNode>().mapTo(list) { it.text }

            if (list.isNotEmpty()) {
                setName(list.removeFirst())

                if (list.isNotEmpty()) {
                    setLore(list)
                }
            }
        }

        is SectionNode -> {
            languageNode.section.getString("name")?.let {
                setName(it)
            }

            languageNode.section.getStringList("lore").let {
                if (it.isNotEmpty()) {
                    setLore(it)
                }
            }
        }
    }

    return this
}

fun ItemStack.localize(languageNode: LanguageNode?, vararg args: String?): ItemStack {
    when (languageNode) {
        is TextNode -> {
            setName(languageNode.format(*args))
        }

        is ListNode -> {
            val list = LinkedList<String>()
            languageNode.list.filterIsInstance<TextNode>().mapTo(list) { it.format(*args) }

            if (list.isNotEmpty()) {
                setName(list.removeFirst())

                if (list.isNotEmpty()) {
                    setLore(list)
                }
            }
        }

        is SectionNode -> {
            languageNode.section.getString("name")?.let {
                setName(it.formatArgs(*args))
            }

            languageNode.section.getStringList("lore").let { loreList ->
                if (loreList.isNotEmpty()) {
                    setLore(loreList.map { it.formatArgs(*args) })
                }
            }
        }
    }

    return this
}

fun ItemStack.setName(name: String): ItemStack {
    val meta = getMeta()
    meta.setDisplayName(name.colored())
    itemMeta = meta
    return this
}

fun ItemStack.setLore(lore: List<String>): ItemStack {
    val meta = getMeta()
    meta.lore = lore.map { it.colored() }
    itemMeta = meta
    return this
}

fun ItemStack.setLore(vararg lore: String): ItemStack =
    setLore(lore.toList())

fun ItemStack.addLore(lore: List<String>): ItemStack {
    val meta = getMeta()
    val existLore = meta.lore ?: ArrayList()
    existLore += lore.colored()
    meta.lore = existLore
    itemMeta = meta
    return this
}

fun ItemStack.addLore(vararg lore: String): ItemStack =
    addLore(lore.toList())

fun ItemStack.insertLore(index: Int, lore: List<String>): ItemStack {
    val meta = getMeta()
    val existLore = meta.lore ?: ArrayList()
    if (index in 0..existLore.size) {
        existLore.addAll(index, lore.colored())
        meta.lore = existLore
        itemMeta = meta
    }
    return this
}

fun ItemStack.insertLore(index: Int, vararg lore: String): ItemStack =
    insertLore(index, lore.toList())

fun ItemStack.setNameAndLore(name: String, lore: List<String>): ItemStack {
    val meta = getMeta()
    meta.lore = lore.map { it.colored() }
    meta.setDisplayName(name.colored())
    itemMeta = meta
    return this
}

fun ItemStack.setNameAndLore(name: String, vararg lore: String): ItemStack =
    setNameAndLore(name, lore.toList())

fun ItemStack?.isItemSimilar(
    item: ItemStack,
    checkLore: Boolean = true,
    checkAmount: Boolean = true,
    checkDurability: Boolean = false
): Boolean {
    return if (this == null) {
        false
    } else if (type != item.type) {
        false
    } else if (checkAmount && amount < item.amount) {
        false
    } else if (checkDurability && durability != item.durability) {
        false
    } else if (hasItemMeta()) {
        val itemMeta = itemMeta ?: return true

        if (item.hasItemMeta()) {
            val itemMeta2 = item.itemMeta ?: return true
            itemMeta.isMetaSimilar(itemMeta2, checkLore)
        } else false
    } else !item.hasItemMeta()
}

fun ItemStack?.isItemSimilar(
    item: Itemable,
    checkLore: Boolean = true,
    checkAmount: Boolean = true,
    checkDurability: Boolean = false
): Boolean = isItemSimilar(item.getItemStack(), checkLore, checkAmount, checkDurability)

fun ItemStack?.isItemSimilar(item: ItemStack): Boolean = isItemSimilar(item, true)

fun ItemStack?.isItemSimilar(item: ItemStack, checkLore: Boolean): Boolean = isItemSimilar(item, checkLore, true)

fun ItemMeta.isMetaSimilar(itemMeta: ItemMeta, checkLore: Boolean = true): Boolean {
    return if (itemMeta.hasDisplayName() != hasDisplayName()) {
        false
    } else if (itemMeta.hasDisplayName() && hasDisplayName() && itemMeta.displayName != displayName) {
        false
    } else if (!checkLore) {
        true
    } else if (itemMeta.hasLore() && hasLore()) {
        val lore = lore ?: return true
        val lore2 = itemMeta.lore ?: return true

        if (lore.isEmpty() && lore2.isEmpty()) return true
        lore.toString() == lore2.toString()
    } else !itemMeta.hasLore() && !hasLore()
}

fun ItemStack.randomAmount(start: Int, end: Int): ItemStack {
    amount = ThreadLocalRandom.current().nextInt(start, end)
    return this
}

fun ItemStack.randomAmount(end: Int): ItemStack = randomAmount(1, end)

fun ItemStack.cloneRandomAmount(start: Int, end: Int): ItemStack =
    clone().also { it.amount = ThreadLocalRandom.current().nextInt(start, end) }

fun ItemStack.cloneRandomAmount(end: Int): ItemStack = randomAmount(1, end)

fun ItemStack.getMeta(): ItemMeta = itemMeta ?: Bukkit.getItemFactory().getItemMeta(type)!!

fun ItemStack.getLoreOrNull(): MutableList<String>? = itemMeta?.lore

fun ItemStack.getLore(): MutableList<String> = getLoreOrNull() ?: mutableListOf()

fun ItemStack.getDisplayNameOrNull(): String? {
    itemMeta?.let {
        if (it.hasDisplayName()) {
            return it.displayName
        }
    }
    
    return null
}

fun ItemStack.getDisplayName(default: String = ""): String =
    getDisplayNameOrNull() ?: default


fun ItemStack.addCurrentLore(player: Player, currentLore: String?): ItemStack {
    return addLore("", player.getLangText("menu-utils-item-current_lore"), currentLore ?: "null")
}

fun ItemStack.addCurrentLore(player: Player, currentLore: List<String>): ItemStack {
    addLore("", player.getLangText("menu-utils-item-current_lore"))
    return addLore(currentLore)
}

fun NamespacedIdItem.toCurrentLocalizedItem(player: Player, currentLore: String?): ItemStack {
    return toLocalizedItem(player).clone().addCurrentLore(player, currentLore)
}

fun NamespacedIdItem.toCurrentLocalizedItem(player: Player, currentLore: List<String>): ItemStack {
    return toLocalizedItem(player).clone().addCurrentLore(player, currentLore)
}
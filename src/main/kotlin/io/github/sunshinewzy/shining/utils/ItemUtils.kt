package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.api.Itemable
import io.github.sunshinewzy.shining.api.lang.LanguageNode
import io.github.sunshinewzy.shining.core.lang.formatArgs
import io.github.sunshinewzy.shining.core.lang.node.ListNode
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.core.lang.node.TextNode
import io.github.sunshinewzy.shining.objects.SItem.Companion.setLore
import io.github.sunshinewzy.shining.objects.SItem.Companion.setName
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem
import java.util.*

fun buildItem(item: Itemable, builder: ItemBuilder.() -> Unit = {}): ItemStack {
    return buildItem(item.getItemStack(), builder)
}

fun ItemStack.localize(languageNode: LanguageNode?): ItemStack {
    when(languageNode) {
        is TextNode -> {
            setName(languageNode.text)
        }

        is ListNode -> {
            val list = LinkedList<String>()
            languageNode.list.filterIsInstance<TextNode>().mapTo(list) { it.text }

            if(list.isNotEmpty()) {
                setName(list.removeFirst())

                if(list.isNotEmpty()) {
                    setLore(list)
                }
            }
        }

        is SectionNode -> {
            languageNode.section.getString("name")?.let {
                setName(it)
            }

            languageNode.section.getStringList("lore").let {
                if(it.isNotEmpty()) {
                    setLore(it)
                }
            }
        }
    }

    return this
}

fun ItemStack.localize(languageNode: LanguageNode?, vararg args: String?): ItemStack {
    when(languageNode) {
        is TextNode -> {
            setName(languageNode.format(*args))
        }

        is ListNode -> {
            val list = LinkedList<String>()
            languageNode.list.filterIsInstance<TextNode>().mapTo(list) { it.format(*args) }

            if(list.isNotEmpty()) {
                setName(list.removeFirst())

                if(list.isNotEmpty()) {
                    setLore(list)
                }
            }
        }

        is SectionNode -> {
            languageNode.section.getString("name")?.let {
                setName(it.formatArgs(*args))
            }

            languageNode.section.getStringList("lore").let { loreList ->
                if(loreList.isNotEmpty()) {
                    setLore(loreList.map { it.formatArgs(*args) })
                }
            }
        }
    }

    return this
}
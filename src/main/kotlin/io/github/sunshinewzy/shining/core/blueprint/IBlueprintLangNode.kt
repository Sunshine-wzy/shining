package io.github.sunshinewzy.shining.core.blueprint

import io.github.sunshinewzy.shining.api.blueprint.IBlueprintNode
import io.github.sunshinewzy.shining.core.lang.getLanguageNode
import io.github.sunshinewzy.shining.core.lang.getLocale
import io.github.sunshinewzy.shining.core.lang.node.ListNode
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.core.lang.node.TextNode
import org.bukkit.command.CommandSender
import java.util.*

interface IBlueprintLangNode : IBlueprintNode {
    
    fun getLanguageNode(): String
    
    override fun getName(sender: CommandSender): String {
        val node = sender.getLanguageNode(getLanguageNode()) ?: return "{${sender.getLocale()}:${getLanguageNode()}}"
        return when (node) {
            is TextNode -> node.text
            is ListNode -> node.list.filterIsInstance<TextNode>().firstOrNull()?.text ?: ""
            is SectionNode -> node.section.getString("name") ?: ""
            else -> ""
        }
    }

    override fun getDescription(sender: CommandSender): List<String> {
        val node = sender.getLanguageNode(getLanguageNode()) ?: return listOf("{${sender.getLocale()}:${getLanguageNode()}}")
        return when (node) {
            is TextNode -> emptyList()
            is ListNode -> {
                val list = LinkedList<String>()
                node.list.filterIsInstance<TextNode>().mapTo(list) { it.text }
                if (list.isNotEmpty()) list.removeFirst()
                list
            }
            is SectionNode -> node.section.getStringList("description")
            else -> emptyList()
        }
    }
    
}
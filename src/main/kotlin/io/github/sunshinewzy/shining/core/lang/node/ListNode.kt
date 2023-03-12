package io.github.sunshinewzy.shining.core.lang.node

import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import taboolib.module.chat.colored

class ListNode(val list: List<LanguageNode>) : LanguageNode {

    fun format(vararg args: String?): List<String> =
        list.filterIsInstance<TextNode>()
            .map { it.format(*args) }

    fun getStringList(): List<String> =
        list.filterIsInstance<TextNode>()
            .map { it.text }

    fun getColoredStringList(): List<String> =
        list.filterIsInstance<TextNode>()
            .map { it.text.colored() }

}
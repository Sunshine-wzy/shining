package io.github.sunshinewzy.shining.core.lang.node

import io.github.sunshinewzy.shining.api.lang.node.IListNode
import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import taboolib.module.chat.colored

class ListNode(override val list: List<LanguageNode>) : IListNode {

    override fun format(vararg args: Any?): List<String> =
        list.filterIsInstance<TextNode>()
            .map { it.format(*args) }

    override fun getStringList(): List<String> =
        list.filterIsInstance<TextNode>()
            .map { it.text }

    override fun getColoredStringList(): List<String> =
        list.filterIsInstance<TextNode>()
            .map { it.text.colored() }

}
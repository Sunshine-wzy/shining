package io.github.sunshinewzy.shining.core.lang.node

import io.github.sunshinewzy.shining.api.lang.LanguageNode

class ListNode(val list: List<LanguageNode>) : LanguageNode {
    
    fun format(vararg args: String?): List<String> =
        list.filterIsInstance<TextNode>()
            .map { it.format(*args) }
    
}
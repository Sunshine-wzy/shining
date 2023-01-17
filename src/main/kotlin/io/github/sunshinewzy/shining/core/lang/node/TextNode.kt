package io.github.sunshinewzy.shining.core.lang.node

import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import io.github.sunshinewzy.shining.core.lang.formatArgs

class TextNode(val text: String) : LanguageNode {
    
    fun format(vararg args: String?): String {
        return text.formatArgs(*args)
    }
    
}
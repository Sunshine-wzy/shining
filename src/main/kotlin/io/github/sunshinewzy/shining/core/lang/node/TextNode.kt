package io.github.sunshinewzy.shining.core.lang.node

import io.github.sunshinewzy.shining.api.lang.node.ITextNode
import io.github.sunshinewzy.shining.core.lang.formatArgs

class TextNode(override val text: String) : ITextNode {

    override fun format(vararg args: Any?): String {
        return text.formatArgs(*args)
    }

}
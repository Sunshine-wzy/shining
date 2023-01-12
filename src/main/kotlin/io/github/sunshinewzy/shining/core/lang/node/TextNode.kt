package io.github.sunshinewzy.shining.core.lang.node

import io.github.sunshinewzy.shining.core.lang.node.LanguageNode.Companion.argRegex
import java.util.*

class TextNode(val text: String) : LanguageNode {
    
    fun format(vararg args: String?): String {
        var flag = true
        for(str in args) {
            if(str != null) {
                flag = false
                break
            }
        }
        if(flag) return text
        
        val list = argRegex.findAll(text).toList()
        if(list.isEmpty()) return text

        val map = TreeMap<Int, Pair<Int, String>>()
        list.forEach { res ->
            args.getOrNull(res.value.substring(1, res.value.lastIndex).toInt())?.let {
                map[res.range.first] = res.range.last to it
            }
        }

        return buildString {
            var last = 0
            for((start, pair) in map) {
                val (end, arg) = pair

                append(text.substring(last, start))
                append(arg)

                last = end + 1
                if(last >= text.length) break
            }

            if(last < text.length) {
                append(text.substring(last))
            }
        }
    }
    
}
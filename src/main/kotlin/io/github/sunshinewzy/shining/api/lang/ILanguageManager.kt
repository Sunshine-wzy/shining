package io.github.sunshinewzy.shining.api.lang

import io.github.sunshinewzy.shining.core.lang.LanguageFile
import io.github.sunshinewzy.shining.core.lang.node.LanguageNode
import io.github.sunshinewzy.shining.core.lang.node.ListNode
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.core.lang.node.TextNode
import java.util.*

interface ILanguageManager {
    
    fun reload()

    fun getLanguageCode(): Set<String>

    fun getLanguageFileMap(): Map<String, LanguageFile>
    
    fun getLanguageFile(locale: String): LanguageFile?
    
    fun getLanguageNode(locale: String, node: String): LanguageNode? =
        getLanguageFile(locale)?.let {
            it.nodeMap[node]
        }
    
    fun getLangTextNode(locale: String, node: String): TextNode? =
        getLanguageNode(locale, node)?.let { 
            it as? TextNode
        }
    
    fun getLangListNode(locale: String, node: String): ListNode? =
        getLanguageNode(locale, node)?.let { 
            it as? ListNode
        }
    
    fun getLangSectionNode(locale: String, node: String): SectionNode? =
        getLanguageNode(locale, node)?.let { 
            it as? SectionNode
        }
    
    fun getLangTextOrNull(locale: String, node: String): String? =
        getLangTextNode(locale, node)?.text
    
    fun getLangText(locale: String, node: String): String =
        getLangTextOrNull(locale, node) ?: "{$locale:$node}"
    
    fun getLangTextOrNull(locale: String, node: String, vararg args: String): String? {
        val text = getLangTextOrNull(locale, node) ?: return null
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

    fun getLangText(locale: String, node: String, vararg args: String): String =
        getLangTextOrNull(locale, node, *args) ?: "{$locale:$node:${args.joinToString()}}"
    
    
    companion object {
        val argRegex = Regex("\\{[0-9]+}")
    }
    
}
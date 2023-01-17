package io.github.sunshinewzy.shining.api.lang

import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import io.github.sunshinewzy.shining.core.lang.LanguageFile
import io.github.sunshinewzy.shining.core.lang.node.ListNode
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.core.lang.node.TextNode

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
    
    fun getLangTextOrNull(locale: String, node: String, vararg args: String?): String? =
        getLangTextNode(locale, node)?.format(*args)

    fun getLangText(locale: String, node: String, vararg args: String?): String =
        getLangTextOrNull(locale, node, *args) ?: "{$locale:$node:${args.joinToString()}}"
    
    fun transfer(source: String): String
    
}
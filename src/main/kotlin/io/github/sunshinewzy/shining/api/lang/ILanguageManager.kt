package io.github.sunshinewzy.shining.api.lang

import io.github.sunshinewzy.shining.api.ShiningConfig
import io.github.sunshinewzy.shining.api.lang.node.IListNode
import io.github.sunshinewzy.shining.api.lang.node.ISectionNode
import io.github.sunshinewzy.shining.api.lang.node.ITextNode
import io.github.sunshinewzy.shining.api.lang.node.LanguageNode

@Suppress("ReplaceCallWithBinaryOperator")
interface ILanguageManager {

    fun reload()

    fun getLanguageCode(): Set<String>

    fun getLanguageFileMap(): Map<String, LanguageFile>

    fun getLanguageFile(locale: String): LanguageFile?

    fun getLanguageNode(locale: String, node: String): LanguageNode? =
        getLanguageFile(locale)?.nodeMap?.get(node) ?:
        if (!locale.equals(ShiningConfig.language))
            getLanguageFile(ShiningConfig.language)?.nodeMap?.get(node)
        else null

    fun getLangTextNode(locale: String, node: String): ITextNode? =
        getLanguageNode(locale, node)?.let {
            it as? ITextNode
        }

    fun getLangListNode(locale: String, node: String): IListNode? =
        getLanguageNode(locale, node)?.let {
            it as? IListNode
        }

    fun getLangSectionNode(locale: String, node: String): ISectionNode? =
        getLanguageNode(locale, node)?.let {
            it as? ISectionNode
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
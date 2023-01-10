package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.api.ShiningConfig
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.lang.node.LanguageNode
import io.github.sunshinewzy.shining.core.lang.node.ListNode
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.core.lang.node.TextNode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun NamespacedId.toNodeString(): String =
    "${namespace.name}-$id"

fun NamespacedId.toNodeString(prefix: String): String =
    if(prefix == "") toNodeString() else "$prefix-${toNodeString()}"

@JvmOverloads
fun NamespacedId.getLanguageNodeOrNull(prefix: String = "", locale: String = ShiningConfig.language): LanguageNode? =
    ShiningLanguageManager.getLanguageNode(this, prefix, locale)

@JvmOverloads
fun NamespacedId.getLanguageNode(prefix: String = "", locale: String = ShiningConfig.language): LanguageNode =
    getLanguageNodeOrNull(prefix, locale) ?: throw LanguageException("Cannot find the node '${toNodeString(prefix)}' in '$locale.yml'.")

fun CommandSender.getLocale(): String =
    if(this is Player) locale else ShiningConfig.language

fun CommandSender.getLanguageNode(node: String): LanguageNode? =
    ShiningLanguageManager.getLanguageNode(getLocale(), node)

fun CommandSender.getLangTextNode(node: String): TextNode? =
    ShiningLanguageManager.getLangTextNode(getLocale(), node)

fun CommandSender.getLangListNode(node: String): ListNode? =
    ShiningLanguageManager.getLangListNode(getLocale(), node)

fun CommandSender.getLangSectionNode(node: String): SectionNode? =
    ShiningLanguageManager.getLangSectionNode(getLocale(), node)

fun CommandSender.getLangTextOrNull(node: String): String? =
    ShiningLanguageManager.getLangTextOrNull(getLocale(), node)

fun CommandSender.getLangText(node: String): String =
    ShiningLanguageManager.getLangText(getLocale(), node)

fun CommandSender.sendLangText(node: String): Boolean =
    getLangTextOrNull(node)?.let { 
        sendMessage(it)
        true
    } ?: false

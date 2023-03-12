package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.ShiningConfig.language
import io.github.sunshinewzy.shining.api.addon.ShiningAddon
import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.lang.node.ListNode
import io.github.sunshinewzy.shining.core.lang.node.SectionNode
import io.github.sunshinewzy.shining.core.lang.node.TextNode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.module.chat.colored
import java.util.*

fun NamespacedId.toNodeString(): String =
    "${namespace.name}-$id"

fun NamespacedId.toNodeString(prefix: String): String =
    if (prefix == "") toNodeString() else "$prefix-${toNodeString()}"

@JvmOverloads
fun NamespacedId.getLanguageNodeOrNull(prefix: String = "", locale: String = language): LanguageNode? =
    ShiningLanguageManager.getLanguageNode(this, prefix, locale)

@JvmOverloads
fun NamespacedId.getLanguageNode(prefix: String = "", locale: String = language): LanguageNode =
    getLanguageNodeOrNull(prefix, locale)
        ?: throw LanguageException("Cannot find the node '${toNodeString(prefix)}' in '$locale.yml'.")


fun getDefaultLanguageNode(node: String): LanguageNode? =
    ShiningLanguageManager.getLanguageNode(language, node)

fun getDefaultLangTextNode(node: String): TextNode? =
    ShiningLanguageManager.getLangTextNode(language, node)

fun getDefaultLangListNode(node: String): ListNode? =
    ShiningLanguageManager.getLangListNode(language, node)

fun getDefaultLangSectionNode(node: String): SectionNode? =
    ShiningLanguageManager.getLangSectionNode(language, node)

fun getDefaultLangTextOrNull(node: String): String? =
    ShiningLanguageManager.getLangTextOrNull(language, node)

fun getDefaultLangText(node: String): String =
    ShiningLanguageManager.getLangText(language, node)

fun getDefaultLangTextOrNull(node: String, vararg args: String?): String? =
    ShiningLanguageManager.getLangTextOrNull(language, node, *args)

fun getDefaultLangText(node: String, vararg args: String?): String =
    ShiningLanguageManager.getLangText(language, node, *args)


fun getDefaultLanguageNode(addon: ShiningAddon, node: String): LanguageNode? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLanguageNode(language, node)

fun getDefaultLangTextNode(addon: ShiningAddon, node: String): TextNode? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangTextNode(language, node)

fun getDefaultLangListNode(addon: ShiningAddon, node: String): ListNode? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangListNode(language, node)

fun getDefaultLangSectionNode(addon: ShiningAddon, node: String): SectionNode? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangSectionNode(language, node)

fun getDefaultLangTextOrNull(addon: ShiningAddon, node: String): String? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangTextOrNull(language, node)

fun getDefaultLangText(addon: ShiningAddon, node: String): String =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangText(language, node)
        ?: throw LanguageException("Cannot find the language manager of the addon '${addon.getName()}'.")

fun getDefaultLangTextOrNull(addon: ShiningAddon, node: String, vararg args: String?): String? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangTextOrNull(language, node, *args)

fun getDefaultLangText(addon: ShiningAddon, node: String, vararg args: String?): String =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangText(language, node, *args)
        ?: throw LanguageException("Cannot find the language manager of the addon '${addon.getName()}'.")


fun CommandSender.getLocale(): String =
    if (this is Player) locale else language

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
        sendMessage(it.colored())
        true
    } ?: false

fun CommandSender.sendPrefixedLangText(node: String, prefix: String = Shining.prefix): Boolean =
    getLangTextOrNull(node)?.let {
        sendMessage("&f[$prefix&f] $it".colored())
        true
    } ?: false

fun CommandSender.getLangTextOrNull(node: String, vararg args: String?): String? =
    ShiningLanguageManager.getLangTextOrNull(getLocale(), node, *args)

fun CommandSender.getLangText(node: String, vararg args: String?): String =
    ShiningLanguageManager.getLangText(getLocale(), node, *args)

fun CommandSender.sendLangText(node: String, vararg args: String?): Boolean =
    getLangTextOrNull(node, *args)?.let {
        sendMessage(it.colored())
        true
    } ?: false

fun CommandSender.sendPrefixedLangText(node: String, prefix: String = Shining.prefix, vararg args: String?): Boolean =
    getLangTextOrNull(node, *args)?.let {
        sendMessage("&f[$prefix&f] $it".colored())
        true
    } ?: false


fun CommandSender.getLanguageNode(addon: ShiningAddon, node: String): LanguageNode? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLanguageNode(getLocale(), node)

fun CommandSender.getLangTextNode(addon: ShiningAddon, node: String): TextNode? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangTextNode(getLocale(), node)

fun CommandSender.getLangListNode(addon: ShiningAddon, node: String): ListNode? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangListNode(getLocale(), node)

fun CommandSender.getLangSectionNode(addon: ShiningAddon, node: String): SectionNode? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangSectionNode(getLocale(), node)

fun CommandSender.getLangTextOrNull(addon: ShiningAddon, node: String): String? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangTextOrNull(getLocale(), node)

fun CommandSender.getLangText(addon: ShiningAddon, node: String): String =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangText(getLocale(), node)
        ?: throw LanguageException("Cannot find the language manager of the addon '${addon.getName()}'.")

fun CommandSender.sendLangText(addon: ShiningAddon, node: String): Boolean =
    getLangTextOrNull(addon, node)?.let {
        sendMessage(it.colored())
        true
    } ?: false

fun CommandSender.sendPrefixedLangText(addon: ShiningAddon, node: String, prefix: String = addon.getPrefix()): Boolean =
    getLangTextOrNull(addon, node)?.let {
        sendMessage("&f[$prefix&f] $it".colored())
        true
    } ?: false

fun CommandSender.getLangTextOrNull(addon: ShiningAddon, node: String, vararg args: String?): String? =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangTextOrNull(getLocale(), node, *args)

fun CommandSender.getLangText(addon: ShiningAddon, node: String, vararg args: String?): String =
    ShiningLanguageManager.getAddonLanguageManager(addon)?.getLangText(getLocale(), node, *args)
        ?: throw LanguageException("Cannot find the language manager of the addon '${addon.getName()}'.")

fun CommandSender.sendLangText(addon: ShiningAddon, node: String, vararg args: String?): Boolean =
    getLangTextOrNull(addon, node, *args)?.let {
        sendMessage(it.colored())
        true
    } ?: false

fun CommandSender.sendLangText(
    addon: ShiningAddon,
    node: String,
    prefix: String = addon.getPrefix(),
    vararg args: String?
): Boolean =
    getLangTextOrNull(addon, node, *args)?.let {
        sendMessage("&f[$prefix&f] $it".colored())
        true
    } ?: false


fun String.formatArgs(vararg args: String?): String {
    val text = this
    var flag = true
    for (str in args) {
        if (str != null) {
            flag = false
            break
        }
    }
    if (flag) return this

    val list = LanguageNode.argRegex.findAll(text).toList()
    if (list.isEmpty()) return text

    val map = TreeMap<Int, Pair<Int, String>>()
    list.forEach { res ->
        args.getOrNull(res.value.substring(1, res.value.lastIndex).toInt())?.let {
            map[res.range.first] = res.range.last to it
        }
    }

    return buildString {
        var last = 0
        for ((start, pair) in map) {
            val (end, arg) = pair

            append(text.substring(last, start))
            append(arg)

            last = end + 1
            if (last >= text.length) break
        }

        if (last < text.length) {
            append(text.substring(last))
        }
    }
}

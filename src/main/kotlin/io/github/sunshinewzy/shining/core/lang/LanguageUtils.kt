package io.github.sunshinewzy.shining.core.lang

import io.github.sunshinewzy.shining.api.ShiningConfig
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.lang.Language
import taboolib.module.lang.TypeText

private val consoleSender: ProxyCommandSender by lazy {
    adaptCommandSender(Bukkit.getConsoleSender())
}


fun toLangTextOrNull(node: String, vararg args: Any): String? {
    Language.languageFile.entries.firstOrNull { it.key.equals(ShiningConfig.language, true) }?.value
        ?: Language.languageFile[Language.default]
        ?: Language.languageFile.values.firstOrNull()?.let { 
            return (it.nodes[node] as? TypeText)?.asText(consoleSender, *args)
        }
    return null
}

fun toLangText(node: String, vararg args: Any): String {
    return toLangTextOrNull(node, args) ?: "{$node}"
}
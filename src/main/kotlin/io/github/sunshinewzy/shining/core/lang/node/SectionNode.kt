package io.github.sunshinewzy.shining.core.lang.node

import io.github.sunshinewzy.shining.api.lang.node.ISectionNode
import io.github.sunshinewzy.shining.core.lang.ShiningLanguageManager.transfer
import io.github.sunshinewzy.shining.core.lang.formatArgs
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.chat.Components

class SectionNode(override val section: ConfigurationSection) : ISectionNode {

    override fun sendJson(sender: CommandSender, vararg args: Any?) {
        val text = section.getString("text") ?: return
        sendJsonTo(text, sender, *args)
    }

    override fun sendPrefixedJson(sender: CommandSender, prefix: String, vararg args: Any?) {
        val text = section.getString("text") ?: return
        sendJsonTo("&f[$prefix&f] $text", sender, *args)
    }


    private fun sendJsonTo(text: String, sender: CommandSender, vararg args: Any?) {
        Components.empty().apply {
            append(transfer(text.formatArgs(*args)))
            section.getString("hover")?.let { hoverText(transfer(it.formatArgs(*args))) }
            section.getString("command")?.let { clickRunCommand(transfer(it.formatArgs(*args))) }
            section.getString("suggest")?.let { clickSuggestCommand(transfer(it.formatArgs(*args))) }
            section.getString("insertion")?.let { clickInsertText(transfer(it.formatArgs(*args))) }
            section.getString("copy")?.let { clickCopyToClipboard(transfer(it.formatArgs(*args))) }
            section.getString("file")?.let { clickOpenFile(transfer(it.formatArgs(*args))) }
            section.getString("url")?.let { clickOpenURL(transfer(it.formatArgs(*args))) }
        }.sendTo(adaptCommandSender(sender))
    }

}
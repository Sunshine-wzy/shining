package io.github.sunshinewzy.shining.core.lang.node

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.lang.node.LanguageNode
import io.github.sunshinewzy.shining.core.lang.ShiningLanguageManager.transfer
import io.github.sunshinewzy.shining.core.lang.formatArgs
import org.bukkit.command.CommandSender
import taboolib.common.platform.function.adaptCommandSender
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.TellrawJson

class SectionNode(val section: ConfigurationSection) : LanguageNode {

    fun sendJson(sender: CommandSender, vararg args: String?) {
        val text = section.getString("text") ?: return
        sendJsonTo(text, sender, *args)
    }

    fun sendPrefixedJson(sender: CommandSender, prefix: String = Shining.prefix, vararg args: String?) {
        val text = section.getString("text") ?: return
        sendJsonTo("&f[$prefix&f] $text", sender, *args)
    }


    private fun sendJsonTo(text: String, sender: CommandSender, vararg args: String?) {
        TellrawJson().sendTo(adaptCommandSender(sender)) {
            append(transfer(text.formatArgs(*args)))
            section.getString("hover")?.let { hoverText(transfer(it.formatArgs(*args))) }
            section.getString("command")?.let { runCommand(transfer(it.formatArgs(*args))) }
            section.getString("suggest")?.let { suggestCommand(transfer(it.formatArgs(*args))) }
            section.getString("insertion")?.let { insertion(transfer(it.formatArgs(*args))) }
            section.getString("copy")?.let { copyToClipboard(transfer(it.formatArgs(*args))) }
            section.getString("file")?.let { openFile(transfer(it.formatArgs(*args))) }
            section.getString("url")?.let { openURL(transfer(it.formatArgs(*args))) }
        }
    }

}
package io.github.sunshinewzy.shining.api.lang.node

import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection

interface ISectionNode : LanguageNode {
    
    val section: ConfigurationSection

    fun sendJson(sender: CommandSender, vararg args: Any?)

    fun sendPrefixedJson(sender: CommandSender, prefix: String, vararg args: Any?)
    
}
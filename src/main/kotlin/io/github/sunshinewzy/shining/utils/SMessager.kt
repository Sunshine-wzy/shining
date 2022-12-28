package io.github.sunshinewzy.shining.utils

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class SMessager(val name: String) {
    
    constructor(plugin: JavaPlugin, color: ChatColor = ChatColor.WHITE) : this(color.toString() + plugin.name)
    
    
    fun send(player: Player, msg: String) {
        player.sendMsg("&f[$name&f] $msg")
    }
    
}
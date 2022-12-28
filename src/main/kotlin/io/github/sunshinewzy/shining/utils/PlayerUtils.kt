package io.github.sunshinewzy.shining.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

val UUID.player: Player?
    get() = Bukkit.getPlayer(this)
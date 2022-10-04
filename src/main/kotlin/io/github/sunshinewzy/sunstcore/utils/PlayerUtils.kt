package io.github.sunshinewzy.sunstcore.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

val UUID.player: Player?
    get() = Bukkit.getPlayer(this)
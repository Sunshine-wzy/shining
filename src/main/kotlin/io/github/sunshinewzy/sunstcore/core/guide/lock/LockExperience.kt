package io.github.sunshinewzy.sunstcore.core.guide.lock

import io.github.sunshinewzy.sunstcore.core.guide.ElementLock
import org.bukkit.entity.Player

class LockExperience(
    var level: Int,
    isConsume: Boolean = true
) : ElementLock("$level 级经验", isConsume) {
    
    
    override fun check(player: Player): Boolean =
        player.level >= level

    override fun consume(player: Player) {
        player.level -= level
    }
    
}
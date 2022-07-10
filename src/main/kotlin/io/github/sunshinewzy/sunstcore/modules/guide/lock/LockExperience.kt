package io.github.sunshinewzy.sunstcore.modules.guide.lock

import io.github.sunshinewzy.sunstcore.modules.guide.ElementLock
import org.bukkit.entity.Player

class LockExperience(val level: Int) : ElementLock("§b$level 级经验") {

    override fun check(player: Player): Boolean =
        player.level >= level

    override fun execute(player: Player): Boolean {
        if(check(player)) {
            player.level -= level
            return true
        }
        
        return false
    }
    
}
package io.github.sunshinewzy.sunstcore.modules.guide.lock

import io.github.sunshinewzy.sunstcore.modules.guide.ElementLock
import org.bukkit.entity.Player

class LockItem : ElementLock("") {

    override fun check(player: Player): Boolean {
        return false
    }

    override fun execute(player: Player): Boolean {
        return false
    }
}
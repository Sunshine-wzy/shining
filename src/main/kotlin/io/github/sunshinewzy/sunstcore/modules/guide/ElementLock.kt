package io.github.sunshinewzy.sunstcore.modules.guide

import org.bukkit.entity.Player

abstract class ElementLock(val description: String) {
    /**
     * @return If the player meets the condition, it returns ture.
     */
    abstract fun check(player: Player): Boolean
    
    abstract fun execute(player: Player): Boolean

}
package io.github.sunshinewzy.shining.core.guide

import org.bukkit.entity.Player

/**
 * @param description If the element is locked, it will be shown in the lore of the symbol.
 * @param isConsume If it is true, the lock will run [consume] when the player is unlocking the element.
 */
abstract class ElementLock(
    var description: String,
    var isConsume: Boolean = true
) {
    /**
     * If the element is locked, it will be run when the player is opening the guide.
     * 
     * @return If the player meets the condition, it returns ture.
     */
    abstract fun check(player: Player): Boolean

    /**
     * When the player is unlocking the element, it will be run if [isConsume] is true and [check] returns true. 
     */
    abstract fun consume(player: Player)

    /**
     * When the player attempted to unlock the element but failed, it would be run.
     * 
     * Override this function only when the [description] is not enough.
     * 
     * For example, you can create a menu to help the player understand the requirement he or she needs to meet.
     */
    open fun tip(player: Player) {
        
    }
    
}
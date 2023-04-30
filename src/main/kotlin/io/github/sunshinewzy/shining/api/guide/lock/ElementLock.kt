package io.github.sunshinewzy.shining.api.guide.lock

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.objects.SItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * The lock of elements in ShiningGuide.
 * The element can be unlocked only when all conditions the lock requires are satisfied.
 * 
 * @param description If the element is locked, it will be shown in the lore of the symbol.
 * @param isConsume If it is true, the lock will run [consume] when the player is unlocking the element.
 */
abstract class ElementLock(
    var description: (Player) -> String,
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
     * Opens an editor to edit the lock.
     */
    abstract fun openEditor(player: Player, team: GuideTeam, state: GuideElementState)
    

    /**
     * When the player attempted to unlock the element but failed, it would be run.
     *
     * Overrides this function only when the [description] is not enough.
     *
     * For example, you can create a menu to help the player understand the requirement he or she needs to meet.
     */
    open fun tip(player: Player) {}

    /**
     * Gets an icon which shows detailed information of the lock.
     */
    open fun getIcon(player: Player): ItemStack =
        SItem(Material.TRIPWIRE_HOOK, description(player))
    
    
    fun switchIsConsume(): Boolean =
        (!isConsume).also { isConsume = it }
    
    
    companion object {
        val itemIsConsume = NamespacedIdItem(Material.APPLE, NamespacedId(Shining, "shining_guide-editor-lock-is_consume"))
        val itemIsConsumeClose = itemIsConsume.toStateItem("close")
        val itemIsConsumeOpen = itemIsConsume.toStateItem("open").shiny()
    }
    
}
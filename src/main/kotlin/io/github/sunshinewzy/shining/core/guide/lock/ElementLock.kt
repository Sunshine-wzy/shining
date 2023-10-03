package io.github.sunshinewzy.shining.core.guide.lock

import io.github.sunshinewzy.shining.api.guide.lock.IElementLock
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Function

/**
 * The lock of elements in ShiningGuide.
 * The element can be unlocked only when all conditions the lock requires are satisfied.
 *
 * @param description If the element is locked, it will be shown in the lore of the symbol.
 * @param isConsume If it is true, the lock will run [consume] when the player is unlocking the element.
 */
abstract class ElementLock(
    override var description: Function<Player, String>,
    override var isConsume: Boolean = true
) : IElementLock {
    
    override fun getIcon(player: Player): ItemStack =
        SItem(Material.TRIPWIRE_HOOK, description.apply(player))
    
    override fun switchIsConsume(): Boolean =
        (!isConsume).also { isConsume = it }

    abstract override fun clone(): ElementLock
    
    
    companion object {
        val itemIsConsumeClose = ShiningIcon.CONSUME_MODE.toStateItem("close")
        val itemIsConsumeOpen = ShiningIcon.CONSUME_MODE.toStateItem("open").shiny()
    }
    
}
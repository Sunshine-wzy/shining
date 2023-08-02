package io.github.sunshinewzy.shining.api.guide.lock

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
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
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY
)
abstract class ElementLock(
    @JsonIgnore var description: (Player) -> String,
    var isConsume: Boolean = true
) : Cloneable {
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
    abstract fun openEditor(player: Player, team: GuideTeam, state: GuideElementState, context: GuideContext)
    

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


    public abstract override fun clone(): ElementLock
    
    
    companion object {
        val itemIsConsumeClose = ShiningIcon.CONSUME_MODE.toStateItem("close")
        val itemIsConsumeOpen = ShiningIcon.CONSUME_MODE.toStateItem("open").shiny()
    }
    
}
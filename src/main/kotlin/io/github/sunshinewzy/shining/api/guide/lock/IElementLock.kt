package io.github.sunshinewzy.shining.api.guide.lock

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Function

/**
 * The lock of elements in ShiningGuide.
 * The element can be unlocked only when all conditions the lock requires are satisfied.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY
)
interface IElementLock : Cloneable {

    @get:JsonIgnore
    var description: Function<Player, String>
    var isConsume: Boolean

    /**
     * If the element is locked, it will be run when the player is opening the guide.
     *
     * @return If the player meets the condition, it returns ture.
     */
    fun check(player: Player): Boolean

    /**
     * When the player is unlocking the element, it will be run if [isConsume] is true and [check] returns true.
     */
    fun consume(player: Player)

    /**
     * Opens an editor to edit the lock.
     */
    fun openEditor(player: Player, team: IGuideTeam, context: GuideContext, state: IGuideElementState)

    /**
     * When the player attempted to unlock the element but failed, it would be run.
     *
     * Overrides this function only when the [description] is not enough.
     *
     * For example, you can create a menu to help the player understand the requirement he or she needs to meet.
     */
    fun tip(player: Player) {}

    /**
     * Gets an icon which shows detailed information of the lock.
     */
    fun getIcon(player: Player): ItemStack

    fun switchIsConsume(): Boolean

    public override fun clone(): IElementLock
    
}
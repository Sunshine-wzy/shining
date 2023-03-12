package io.github.sunshinewzy.shining.api.guide.state

import io.github.sunshinewzy.shining.core.guide.GuideTeam
import org.bukkit.entity.Player

/**
 * Represents a captured state of an element, which can describe properties of the element.
 */
interface IGuideElementState {

    /**
     * Updates the state to the element if exists.
     * 
     * @return True when it succeeds.
     */
    fun update(): Boolean

    /**
     * Opens an editor to edit the state.
     */
    fun openEditor(player: Player, team: GuideTeam)

}
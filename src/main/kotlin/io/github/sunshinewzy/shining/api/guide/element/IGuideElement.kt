package io.github.sunshinewzy.shining.api.guide.element

import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.ElementCondition
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface IGuideElement {
    
    fun getId(): NamespacedId
    
    fun getName(): String

    fun open(player: Player, team: GuideTeam, previousElement: IGuideElement? = null)

    fun back(player: Player, team: GuideTeam)

    fun unlock(player: Player, team: GuideTeam): Boolean

    fun update(state: IGuideElementState): Boolean

    fun getState(): IGuideElementState

    fun getCondition(team: GuideTeam): ElementCondition

    fun getSymbolByCondition(player: Player, team: GuideTeam, condition: ElementCondition): ItemStack

    fun isTeamCompleted(team: GuideTeam): Boolean
    
}
package io.github.sunshinewzy.shining.api.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.core.guide.team.GuideTeamElementData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface IGuideElement {

    fun getId(): NamespacedId

    fun getDescription(): ElementDescription

    fun getSymbol(): ItemStack

    /**
     * Let the [player] in [team] open the element.
     * 
     * @param team If it is [GuideTeam.CompletedTeam], all elements in the element will be shown completed.
     * @param previousElement The previous element which calls the current element.
     */
    fun open(player: Player, team: GuideTeam, previousElement: IGuideElement? = null, context: GuideContext = EmptyGuideContext)

    /**
     * Let the [player] open the previous element.
     */
    fun back(player: Player, team: GuideTeam, context: GuideContext = EmptyGuideContext)

    /**
     * Let the [player] try to unlock the element.
     * 
     * @return True when the [player] unlock the element successfully.
     */
    fun unlock(player: Player, team: GuideTeam): Boolean
    
    fun complete(player: Player, team: GuideTeam, isSilent: Boolean = false)

    fun reward(player: Player)
    
    fun saveToState(state: IGuideElementState): Boolean

    fun getState(): IGuideElementState

    fun update(state: IGuideElementState, isMerge: Boolean): Boolean
    
    fun update(state: IGuideElementState): Boolean = update(state, false)

    suspend fun getTeamData(team: GuideTeam): GuideTeamElementData
    
    suspend fun getCondition(team: GuideTeam): ElementCondition

    suspend fun getSymbolByCondition(player: Player, team: GuideTeam, condition: ElementCondition): ItemStack
    
    suspend fun getUnlockedSymbol(player: Player): ItemStack =
        getSymbolByCondition(player, GuideTeam.CompletedTeam, ElementCondition.UNLOCKED)
    
    suspend fun isTeamCompleted(team: GuideTeam): Boolean

    suspend fun isTeamUnlocked(team: GuideTeam): Boolean

    suspend fun isTeamDependencyCompleted(team: GuideTeam): Boolean

    /**
     * Register the element
     * 
     * @return If there is the same id in [io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry],
     * it will return [io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry.getElement]
     * with parameter id by [getId]
     */
    fun register(): IGuideElement
    
}
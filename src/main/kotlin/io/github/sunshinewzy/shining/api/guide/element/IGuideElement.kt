package io.github.sunshinewzy.shining.api.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.lock.IElementLock
import io.github.sunshinewzy.shining.api.guide.reward.IGuideReward
import io.github.sunshinewzy.shining.api.guide.settings.RepeatableSettings
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.CompletedGuideTeam
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeamData
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

interface IGuideElement {

    fun getId(): NamespacedId

    fun getDescription(): ElementDescription

    fun getSymbol(): ItemStack
    
    fun getDependencies(): Map<NamespacedId, IGuideElement>
    
    fun getLocks(): List<IElementLock>

    fun getRewards(): List<IGuideReward>
    
    fun getRepeatableSettings(): RepeatableSettings

    /**
     * Let the [player] in [team] open the element.
     * 
     * @param team If it is [CompletedGuideTeam], all elements in the element will be shown completed.
     * @param previousElement The previous element which calls the current element.
     */
    fun open(player: Player, team: IGuideTeam, previousElement: IGuideElement? = null, context: GuideContext = EmptyGuideContext)

    /**
     * Let the [player] open the previous element.
     */
    fun back(player: Player, team: IGuideTeam, context: GuideContext = EmptyGuideContext)

    /**
     * Let the [player] try to unlock the element.
     * 
     * @return True when the [player] unlock the element successfully.
     */
    fun unlock(player: Player, team: IGuideTeam): Boolean
    
    fun complete(player: Player, team: IGuideTeam, isSilent: Boolean = false)
    
    fun fail(player: Player)

    fun reward(player: Player)
    
    fun saveToState(state: IGuideElementState): Boolean

    fun getState(): IGuideElementState

    fun update(state: IGuideElementState, isMerge: Boolean): Boolean
    
    fun update(state: IGuideElementState): Boolean = update(state, false)

    fun getTeamDataFuture(team: IGuideTeam): CompletableFuture<IGuideTeamData>
    
    fun getConditionFuture(team: IGuideTeam): CompletableFuture<ElementCondition>

    fun getSymbolByConditionFuture(player: Player, team: IGuideTeam, condition: ElementCondition): CompletableFuture<ItemStack>
    
    fun getUnlockedSymbolFuture(player: Player): CompletableFuture<ItemStack> =
        getSymbolByConditionFuture(player, CompletedGuideTeam.getInstance(), ElementCondition.UNLOCKED)
    
    fun isTeamCompletedFuture(team: IGuideTeam): CompletableFuture<Boolean>

    fun isTeamUnlockedFuture(team: IGuideTeam): CompletableFuture<Boolean>

    fun isTeamDependencyCompletedFuture(team: IGuideTeam): CompletableFuture<Boolean>

    /**
     * Register the element
     * 
     * @return If there is the same id in [io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry],
     * it will return [io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry.getElement]
     * with parameter id by [getId]
     */
    fun register(): IGuideElement

    fun registerDependency(element: IGuideElement): IGuideElement

    fun registerLock(lock: IElementLock): IGuideElement
    
    fun registerReward(reward: IGuideReward): IGuideElement
    
}
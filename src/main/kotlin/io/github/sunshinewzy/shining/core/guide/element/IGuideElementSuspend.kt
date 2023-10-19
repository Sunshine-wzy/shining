package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.team.CompletedGuideTeam
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeamData
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

interface IGuideElementSuspend : IGuideElement {

    suspend fun getTeamData(team: IGuideTeam): IGuideTeamData

    suspend fun getCondition(team: IGuideTeam): ElementCondition

    suspend fun getSymbolByCondition(player: Player, team: IGuideTeam, condition: ElementCondition): ItemStack

    suspend fun getUnlockedSymbol(player: Player): ItemStack =
        getSymbolByCondition(player, CompletedGuideTeam.getInstance(), ElementCondition.UNLOCKED)

    suspend fun isTeamCompleted(team: IGuideTeam): Boolean

    suspend fun isTeamUnlocked(team: IGuideTeam): Boolean

    suspend fun isTeamDependencyCompleted(team: IGuideTeam): Boolean

    suspend fun getTeamRepeatablePeriodRemainingTime(team: IGuideTeam): Long

    suspend fun getTeamRemainingTime(team: IGuideTeam): Long

    suspend fun canTeamComplete(team: IGuideTeam): Boolean

    suspend fun checkComplete(player: Player, team: IGuideTeam): Boolean

    suspend fun tryToComplete(player: Player, team: IGuideTeam, silent: Boolean): Boolean

    suspend fun tryToComplete(player: Player, team: IGuideTeam): Boolean =
        tryToComplete(player, team, false)
    
    override fun getTeamDataFuture(team: IGuideTeam): CompletableFuture<IGuideTeamData> =
        ShiningDispatchers.futureIO { getTeamData(team) }

    override fun getConditionFuture(team: IGuideTeam): CompletableFuture<ElementCondition> =
        ShiningDispatchers.futureIO { getCondition(team) }

    override fun getSymbolByConditionFuture(player: Player, team: IGuideTeam, condition: ElementCondition): CompletableFuture<ItemStack> =
        ShiningDispatchers.futureIO { getSymbolByCondition(player, team, condition) }

    override fun isTeamCompletedFuture(team: IGuideTeam): CompletableFuture<Boolean> =
        ShiningDispatchers.futureIO { isTeamCompleted(team) }

    override fun isTeamUnlockedFuture(team: IGuideTeam): CompletableFuture<Boolean> =
        ShiningDispatchers.futureIO { isTeamUnlocked(team) }

    override fun isTeamDependencyCompletedFuture(team: IGuideTeam): CompletableFuture<Boolean> =
        ShiningDispatchers.futureIO { isTeamDependencyCompleted(team) }

    override fun getTeamRepeatablePeriodRemainingTimeFuture(team: IGuideTeam): CompletableFuture<Long> =
        ShiningDispatchers.futureIO { getTeamRepeatablePeriodRemainingTime(team) }

    override fun getTeamRemainingTimeFuture(team: IGuideTeam): CompletableFuture<Long> =
        ShiningDispatchers.futureIO { getTeamRemainingTime(team) }

    override fun canTeamCompleteFuture(team: IGuideTeam): CompletableFuture<Boolean> =
        ShiningDispatchers.futureIO { canTeamComplete(team) }

    override fun checkCompleteFuture(player: Player, team: IGuideTeam): CompletableFuture<Boolean> =
        ShiningDispatchers.futureIO { checkComplete(player, team) }

    override fun tryToCompleteFuture(player: Player, team: IGuideTeam, silent: Boolean): CompletableFuture<Boolean> =
        ShiningDispatchers.futureIO { tryToComplete(player, team, silent) }
    
}
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

    override fun getTeamDataFuture(team: IGuideTeam): CompletableFuture<IGuideTeamData> =
        ShiningDispatchers.futureDB { getTeamData(team) }

    override fun getConditionFuture(team: IGuideTeam): CompletableFuture<ElementCondition> =
        ShiningDispatchers.futureDB { getCondition(team) }

    override fun getSymbolByConditionFuture(player: Player, team: IGuideTeam, condition: ElementCondition): CompletableFuture<ItemStack> =
        ShiningDispatchers.futureDB { getSymbolByCondition(player, team, condition) }

    override fun isTeamCompletedFuture(team: IGuideTeam): CompletableFuture<Boolean> =
        ShiningDispatchers.futureDB { isTeamCompleted(team) }

    override fun isTeamUnlockedFuture(team: IGuideTeam): CompletableFuture<Boolean> =
        ShiningDispatchers.futureDB { isTeamUnlocked(team) }

    override fun isTeamDependencyCompletedFuture(team: IGuideTeam): CompletableFuture<Boolean> =
        ShiningDispatchers.futureDB { isTeamDependencyCompleted(team) }
    
}
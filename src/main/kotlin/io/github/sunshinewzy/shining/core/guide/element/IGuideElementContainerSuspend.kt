package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import java.util.concurrent.CompletableFuture

interface IGuideElementContainerSuspend : IGuideElementContainer, IGuideElementSuspend {

    suspend fun getElementsByCondition(team: IGuideTeam, condition: ElementCondition, isDeep: Boolean = false): List<IGuideElement>

    override fun getElementsByConditionFuture(team: IGuideTeam, condition: ElementCondition, isDeep: Boolean): CompletableFuture<List<IGuideElement>> =
        ShiningDispatchers.futureDB { getElementsByCondition(team, condition, isDeep) }

}
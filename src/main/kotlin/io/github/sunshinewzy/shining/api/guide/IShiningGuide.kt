package io.github.sunshinewzy.shining.api.guide

import io.github.sunshinewzy.shining.api.guide.context.EmptyGuideContext
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementPriorityContainer
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

interface IShiningGuide : IGuideElementPriorityContainer {
    
    fun reload()

    fun openMainMenu(player: Player, context: GuideContext)
    
    fun openMainMenu(player: Player) {
        openMainMenu(player, EmptyGuideContext)
    }

    fun openMainMenu(player: Player, team: IGuideTeam, context: GuideContext)

    fun openMainMenu(player: Player, team: IGuideTeam) {
        openMainMenu(player, team, EmptyGuideContext)
    }

    fun openLastElement(player: Player, context: GuideContext)

    fun openLastElement(player: Player) {
        openLastElement(player, EmptyGuideContext)
    }

    fun openLastElement(player: Player, team: IGuideTeam, context: GuideContext)

    fun openLastElement(player: Player, team: IGuideTeam) {
        openLastElement(player, team, EmptyGuideContext)
    }

    fun openCompletedMainMenu(player: Player, context: GuideContext)

    fun openCompletedMainMenu(player: Player) {
        openCompletedMainMenu(player, EmptyGuideContext)
    }

    fun openCompletedLastElement(player: Player, context: GuideContext)

    fun openCompletedLastElement(player: Player) {
        openCompletedLastElement(player, EmptyGuideContext)
    }

    fun recordLastOpenElement(uuid: UUID, element: IGuideElement)

    fun recordLastOpenElement(player: Player, element: IGuideElement) {
        recordLastOpenElement(player.uniqueId, element)
    }

    fun recordElementAdditionalContext(uuid: UUID, element: IGuideElement, context: GuideContext)

    fun recordElementAdditionalContext(player: Player, element: IGuideElement, context: GuideContext) {
        recordElementAdditionalContext(player.uniqueId, element, context)
    }

    fun getElementAdditionalContext(uuid: UUID, element: IGuideElement): GuideContext?

    fun getElementAdditionalContext(player: Player, element: IGuideElement): GuideContext? =
        getElementAdditionalContext(player.uniqueId, element)

    fun fireworkCongratulate(player: Player)

    fun getItemStack(): ItemStack
    
}
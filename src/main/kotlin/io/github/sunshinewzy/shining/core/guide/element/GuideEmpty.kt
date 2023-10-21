package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.state.GuideEmptyState
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit

open class GuideEmpty : GuideElement {
    
    constructor(
        id: NamespacedId,
        description: ElementDescription,
        symbol: ItemStack
    ) : super(id, description, symbol)
    
    constructor() : super()

    override fun open(player: Player, team: IGuideTeam, previousElement: IGuideElement?, context: GuideContext) {
        ShiningDispatchers.launchDB {
            if (canTeamComplete(team)) {
                submit {
                    complete(player, team)
                }
            }
        }
    }

    override fun openMenu(player: Player, team: IGuideTeam, context: GuideContext) {}

    override suspend fun checkComplete(player: Player, team: IGuideTeam): Boolean {
        return true
    }

    override fun getState(): IGuideElementState =
        GuideEmptyState().correlateElement(this)

    override fun register(): GuideEmpty = super.register() as GuideEmpty
    
}
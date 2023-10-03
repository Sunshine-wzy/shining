package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

open class GuideMachine : GuideElement {
    
    constructor(id: NamespacedId, description: ElementDescription, symbol: ItemStack) : super(id, description, symbol)
    
    constructor() : super()
    

    override fun openMenu(player: Player, team: IGuideTeam, context: GuideContext) {
        TODO("Not yet implemented")
    }

    override fun getState(): IGuideElementState {
        TODO("Not yet implemented")
    }

    override fun register(): GuideMachine = super.register() as GuideMachine
    
}
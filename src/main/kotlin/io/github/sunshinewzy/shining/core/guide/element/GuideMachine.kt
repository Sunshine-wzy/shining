package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GuideMachine(id: NamespacedId, description: ElementDescription, symbol: ItemStack) :
    GuideElement(id, description, symbol) {


    override fun openMenu(player: Player, team: GuideTeam) {

    }

    override fun getState(): IGuideElementState {
        TODO("Not yet implemented")
    }

}
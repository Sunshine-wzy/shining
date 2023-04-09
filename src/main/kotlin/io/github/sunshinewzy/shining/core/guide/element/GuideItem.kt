package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

open class GuideItem(id: NamespacedId, description: ElementDescription, item: ItemStack) : GuideElement(id, description, item) {


    override fun openMenu(player: Player, team: GuideTeam, context: GuideContext) {
        TODO("Not yet implemented")
    }

    override fun getState(): IGuideElementState {
        TODO("Not yet implemented")
    }

}
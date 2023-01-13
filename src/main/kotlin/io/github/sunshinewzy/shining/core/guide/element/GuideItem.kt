package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.core.guide.GuideElement
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GuideItem(id: String, item: ItemStack) : GuideElement(id, item) {

    
    
    
    override fun openAction(player: Player, team: GuideTeam) {
        
    }
    
}
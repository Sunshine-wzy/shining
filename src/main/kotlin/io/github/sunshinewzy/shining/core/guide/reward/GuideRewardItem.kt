package io.github.sunshinewzy.shining.core.guide.reward

import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.reward.IGuideReward
import io.github.sunshinewzy.shining.api.item.universal.UniversalItem
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.utils.giveItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@JsonTypeName("item")
class GuideRewardItem(val items: MutableList<UniversalItem>) : IGuideReward {

    constructor() : this(ArrayList())
    
    
    override fun reward(player: Player) {
        items.forEach { 
            player.giveItem(it.getItemStack())
        }
    }

    override fun getIcon(player: Player): ItemStack = itemIcon.toLocalizedItem(player)

    override fun openEditor(player: Player, team: GuideTeam, context: GuideContext, state: GuideElementState) {
        TODO("Not yet implemented")
    }

    override fun clone(): GuideRewardItem {
        return GuideRewardItem(ArrayList(items))
    }
    
    
    companion object {
        private val itemIcon = NamespacedIdItem(Material.ITEM_FRAME, NamespacedId(Shining, "shining_guide-reward-item-icon"))
    }
    
}
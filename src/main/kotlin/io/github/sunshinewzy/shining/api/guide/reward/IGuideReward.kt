package io.github.sunshinewzy.shining.api.guide.reward

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import io.github.sunshinewzy.shining.core.guide.team.GuideTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY
)
interface IGuideReward : Cloneable {
    
    fun reward(player: Player)
    
    @JsonIgnore
    fun getIcon(player: Player): ItemStack
    
    fun openEditor(player: Player, team: GuideTeam, context: GuideContext, state: GuideElementState)

    public override fun clone(): IGuideReward
    
}
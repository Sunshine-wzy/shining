package io.github.sunshinewzy.shining.api.guide.reward

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.sunshinewzy.shining.api.guide.GuideContext
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
    
    fun openEditor(player: Player, context: GuideContext)

    public override fun clone(): IGuideReward
    
}
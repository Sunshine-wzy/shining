package io.github.sunshinewzy.shining.api

import io.github.sunshinewzy.shining.api.guide.element.IGuideElementRegistry
import io.github.sunshinewzy.shining.api.guide.reward.IGuideReward
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.item.universal.UniversalItem

interface IShiningAPI {

    fun getUniversalItemRegistry(): IClassRegistry<UniversalItem>

    fun getGuideElementRegistry(): IGuideElementRegistry
    
    fun getGuideElementStateRegistry(): IClassRegistry<IGuideElementState>
    
    fun getGuideRewardRegistry(): IClassRegistry<IGuideReward>
    
}
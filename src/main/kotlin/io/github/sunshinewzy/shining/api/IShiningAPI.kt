package io.github.sunshinewzy.shining.api

import io.github.sunshinewzy.shining.api.guide.IShiningGuide
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementRegistry
import io.github.sunshinewzy.shining.api.guide.reward.IGuideReward
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeamManager
import io.github.sunshinewzy.shining.api.machine.IMachineRegistry
import io.github.sunshinewzy.shining.api.universal.item.UniversalItem

interface IShiningAPI {

    fun getUniversalItemRegistry(): IClassRegistry<UniversalItem>

    fun getGuideElementRegistry(): IGuideElementRegistry
    
    fun getGuideElementStateRegistry(): IClassRegistry<IGuideElementState>
    
    fun getGuideRewardRegistry(): IClassRegistry<IGuideReward>
    
    fun getShiningGuide(): IShiningGuide
    
    fun getGuideTeamManager(): IGuideTeamManager
    
    fun getMachineRegistry(): IMachineRegistry
    
}
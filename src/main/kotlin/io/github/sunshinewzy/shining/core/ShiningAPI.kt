package io.github.sunshinewzy.shining.core

import io.github.sunshinewzy.shining.api.IClassRegistry
import io.github.sunshinewzy.shining.api.IShiningAPI
import io.github.sunshinewzy.shining.api.blueprint.IBlueprintEditor
import io.github.sunshinewzy.shining.api.guide.IShiningGuide
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementRegistry
import io.github.sunshinewzy.shining.api.guide.reward.IGuideReward
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeamManager
import io.github.sunshinewzy.shining.api.machine.IMachineRegistry
import io.github.sunshinewzy.shining.api.universal.item.IUniversalItemRegistry
import io.github.sunshinewzy.shining.core.blueprint.BlueprintEditor
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.guide.reward.GuideRewardRegistry
import io.github.sunshinewzy.shining.core.guide.state.GuideElementStateRegistry
import io.github.sunshinewzy.shining.core.guide.team.GuideTeamManager
import io.github.sunshinewzy.shining.core.machine.MachineRegistry
import io.github.sunshinewzy.shining.core.universal.item.UniversalItemRegistry

class ShiningAPI : IShiningAPI {

    override fun getUniversalItemRegistry(): IUniversalItemRegistry = UniversalItemRegistry

    override fun getGuideElementRegistry(): IGuideElementRegistry = GuideElementRegistry
    
    override fun getGuideElementStateRegistry(): IClassRegistry<IGuideElementState> = GuideElementStateRegistry

    override fun getGuideRewardRegistry(): IClassRegistry<IGuideReward> = GuideRewardRegistry

    override fun getShiningGuide(): IShiningGuide = ShiningGuide

    override fun getGuideTeamManager(): IGuideTeamManager = GuideTeamManager

    override fun getMachineRegistry(): IMachineRegistry = MachineRegistry

    override fun getBlueprintEditor(): IBlueprintEditor = BlueprintEditor
    
}
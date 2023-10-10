package io.github.sunshinewzy.shining.objects.task

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.event.ShiningDataLoadingCompleteEvent
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.api.objects.coordinate.Coordinate2D
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.element.GuideItem
import io.github.sunshinewzy.shining.core.guide.element.GuideMap
import io.github.sunshinewzy.shining.core.guide.reward.GuideRewardItem
import io.github.sunshinewzy.shining.core.item.ConsumableItemGroup
import io.github.sunshinewzy.shining.core.lang.getDefaultLangText
import io.github.sunshinewzy.shining.core.machine.ShiningMachineWrench
import io.github.sunshinewzy.shining.core.universal.item.DictionaryUniversalItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent

object ShiningGuideTask {
    
    @SubscribeEvent
    fun onDataLoadingComplete(event: ShiningDataLoadingCompleteEvent) {
        if (System.getProperty("shining.debug") != "true") return
        
        guideTaskMap.registerElement(wrenchItem, Coordinate2D(1, 0))
        
        guideTaskMap.register()
        ShiningGuide.registerElement(guideTaskMap)
    }

    val guideTaskMap = GuideMap(
        NamespacedId(Shining, "shining_guide_task"),
        ElementDescription(
            getDefaultLangText("task-shining_guide_task-map-description-name"),
            getDefaultLangText("task-shining_guide_task-map-description-lore")
        ),
        ItemStack(Material.CRAFTING_TABLE)
    )
    
    val wrenchItem = GuideItem(
        NamespacedId(Shining, "shining_guide_task-wrench"),
        ElementDescription("&b扳手", "&7构建机器的必备工具"),
        ItemStack(Material.BONE),
        ConsumableItemGroup()
    ).apply { 
        registerReward(GuideRewardItem(DictionaryUniversalItem(ShiningMachineWrench.getDictionaryItem())))
    }
    
}
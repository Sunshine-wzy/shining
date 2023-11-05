package io.github.sunshinewzy.shining.api.universal.item

import io.github.sunshinewzy.shining.api.IClassRegistry
import io.github.sunshinewzy.shining.api.guide.context.GuideContext
import org.bukkit.entity.Player

interface IUniversalItemRegistry : IClassRegistry<UniversalItem> {
    
    fun openCreator(player: Player, context: GuideContext)
    
}
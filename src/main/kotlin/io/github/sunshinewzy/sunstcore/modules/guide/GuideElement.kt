package io.github.sunshinewzy.sunstcore.modules.guide

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class GuideElement(val name: String, val symbol: ItemStack) {
    
    private val dependencies = ArrayList<GuideElement>()
    private val successors = ArrayList<GuideElement>()
    private val previousElementMap = HashMap<UUID, GuideElement>()
    
    
    fun open(player: Player, previous: GuideElement) {
        previousElementMap[player.uniqueId] = previous
        
        openAction(player)
    }
    
    protected abstract fun openAction(player: Player)
    
    fun back(player: Player) {
        
        SGuide.open(player)
        
    }
    
    fun getConditionName() {
        
    }
    
}
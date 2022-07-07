package io.github.sunshinewzy.sunstcore.modules.guide

import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.getLore
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.getSMeta
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.setLore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class GuideElement(
    name: String,
    val symbol: ItemStack
) {
    val name: String
    
    init {
        this.name = name.uppercase()
    }
    
    
    private val dependencies: MutableList<GuideElement> = LinkedList()
    private val successors: MutableList<GuideElement> = LinkedList()
    private val previousElementMap = HashMap<UUID, GuideElement>()
    
    
    val completedPlayers = HashSet<UUID>()
    
    val lockedSymbol: ItemStack by lazy {
        SItem(
            Material.BARRIER,
            symbol.getSMeta().displayName,
            name,
            LOCKED_TEXT,
            ""
        )
    }
    val completedSymbol: ItemStack by lazy {
        val symbolItem = symbol.clone()
        val loreList = arrayListOf("&a已完成", "")
        loreList += symbolItem.getLore()
        symbolItem.setLore(loreList)
    }
    
    
    fun open(player: Player, previous: GuideElement) {
        previousElementMap[player.uniqueId] = previous
        
        openAction(player)
    }
    
    protected abstract fun openAction(player: Player)
    
    fun back(player: Player) {
        
        SGuide.open(player)
        
    }

    fun isPlayerCompleted(player: Player): Boolean = completedPlayers.contains(player.uniqueId)
    
    fun isPlayerUnlocked(player: Player): Boolean {
        for(dependency in dependencies) {
            if(!dependency.isPlayerCompleted(player)) {
                return false
            }
        }
        
        return true
    }
    
    fun getConditionSymbol(player: Player): ItemStack =
        if(isPlayerCompleted(player)) {
            completedSymbol
        } else if(isPlayerUnlocked(player)) {
            symbol
        } else {
            lockedSymbol
        }
    
    
    companion object {
        const val LOCKED_TEXT = "&4&l已锁定"
    }
    
}
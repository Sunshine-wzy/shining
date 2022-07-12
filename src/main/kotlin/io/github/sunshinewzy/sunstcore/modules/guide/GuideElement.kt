package io.github.sunshinewzy.sunstcore.modules.guide

import io.github.sunshinewzy.sunstcore.modules.guide.ElementCondition.*
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.getDisplayName
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.getLore
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.getMeta
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.setLore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class GuideElement(
    id: String,
    val symbol: ItemStack
) {
    val id: String
    val name = symbol.getDisplayName(id)
    
    init {
        this.id = id.uppercase()
    }
    
    
    private val dependencies: MutableList<GuideElement> = LinkedList()
    private val previousElementMap = HashMap<UUID, GuideElement>()
    private val locks = LinkedList<ElementLock>()
    
    
    val completedPlayers = HashSet<UUID>()
    
    val completedSymbol: ItemStack by lazy {
        val symbolItem = symbol.clone()
        val loreList = LinkedList<String>()
        loreList += COMPLETE_TEXT
        loreList += ""
        loreList += symbolItem.getLore()
        symbolItem.setLore(loreList)
    }
    val lockedSymbol: ItemStack by lazy {
        SItem(
            Material.BARRIER,
            symbol.getMeta().displayName,
            "&7$id",
            LOCKED_TEXT,
            "",
            "&a> 解锁条件",
            ""
        )
    }
    
    
    fun open(player: Player, previous: GuideElement?) {
        if(previous != null)
            previousElementMap[player.uniqueId] = previous
        
        openAction(player)
    }
    
    protected abstract fun openAction(player: Player)
    
    fun back(player: Player) {
        previousElementMap[player.uniqueId]?.let { 
            it.open(player, null)
            return
        }
        
        SGuide.open(player)
    }

    fun isPlayerCompleted(player: Player): Boolean = completedPlayers.contains(player.uniqueId)
    
    fun isPlayerDependencyUnlocked(player: Player): Boolean {
        for(dependency in dependencies) {
            if(!dependency.isPlayerCompleted(player)) {
                return false
            }
        }

        return true
    }
    
    fun isPlayerLockUnlocked(player: Player): Boolean {
        for (lock in locks) {
            if(!lock.check(player))
                return false
        }
        
        return true
    }
    
    fun getCondition(player: Player): ElementCondition =
        if(isPlayerCompleted(player)) {
            COMPLETE
        } else if(!isPlayerDependencyUnlocked(player)) {
            LOCKED_DEPENDENCY
        } else if(!isPlayerLockUnlocked(player)) {
            LOCKED_LOCK
        } else {
            UNLOCKED
        }
    
    fun getSymbolByCondition(player: Player, condition: ElementCondition): ItemStack =
        when(condition) {
            COMPLETE -> completedSymbol
            
            UNLOCKED -> symbol
            
            LOCKED_DEPENDENCY -> {
                val theSymbol = lockedSymbol.clone()
                val meta = theSymbol.getMeta()
                
                val lore = meta.lore ?: mutableListOf()
                lore += "§f> 请先完成下列元素"
                lore += ""
                dependencies.forEach { 
                    if(!it.isPlayerCompleted(player)) {
                        lore += it.name
                    }
                }
                meta.lore = lore
                
                theSymbol.itemMeta = meta
                theSymbol
            }
            
            LOCKED_LOCK -> {
                val theSymbol = lockedSymbol.clone()
                val meta = theSymbol.getMeta()
                
                val lore = meta.lore ?: mutableListOf()
                locks.forEach { 
                    if(!it.check(player)) {
                        lore += "§7需要 ${it.description}"
                    }
                }
                meta.lore = lore
                
                theSymbol.itemMeta = meta
                theSymbol
            }
        }
    
    
    fun registerDependency(element: GuideElement) {
        dependencies += element
    }
    
    fun registerLock(lock: ElementLock) {
        locks += lock
    }
    
    
    companion object {
        const val LOCKED_TEXT = "&4&l已锁定"
        const val COMPLETE_TEXT = "&a&l已完成"
    }
    
}
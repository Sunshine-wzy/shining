package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.core.guide.ElementCondition.*
import io.github.sunshinewzy.shining.core.guide.data.ElementPlayerData
import io.github.sunshinewzy.shining.interfaces.Updatable
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.SItem.Companion.getDisplayName
import io.github.sunshinewzy.shining.objects.SItem.Companion.getLore
import io.github.sunshinewzy.shining.objects.SItem.Companion.getMeta
import io.github.sunshinewzy.shining.objects.SItem.Companion.setLore
import io.github.sunshinewzy.shining.utils.sendMsg
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.reflect.KProperty

abstract class GuideElement(
    id: String,
    val symbol: ItemStack
) {
    val id: String
    var name = symbol.getDisplayName(id)
    
    init {
        this.id = id.uppercase()
    }
    
    
    private val dependencies: MutableList<GuideElement> = LinkedList()
    private val locks = LinkedList<ElementLock>()
    private val symbolHandlers = arrayListOf<Updatable>()
    
    private val previousElementMap = HashMap<UUID, GuideElement>()
    private val teamDataMap = HashMap<UUID, ElementPlayerData>()

    private val completedSymbol: ItemStack by SymbolItemDelegate {
        val symbolItem = symbol.clone()
        val loreList = LinkedList<String>()
        loreList += COMPLETE_TEXT
        loreList += ""
        loreList += symbolItem.getLore()
        symbolItem.setLore(loreList)
    }
    private val lockedSymbol: ItemStack by SymbolItemDelegate {
        SItem(
            Material.BARRIER,
            symbol.getMeta().displayName,
            "&7$id",
            LOCKED_TEXT,
            ""
        )
    }
    
    
    fun open(player: Player, previousElement: GuideElement? = null) {
        if(previousElement != null)
            previousElementMap[player.uniqueId] = previousElement
        
        ShiningGuide.playerLastOpenElementMap[player.uniqueId] = this
        openAction(player)
    }
    
    protected abstract fun openAction(player: Player)
    
    fun back(player: Player) {
        previousElementMap[player.uniqueId]?.let { 
            it.open(player, null)
            return
        }
        
        ShiningGuide.open(player)
    }
    
    fun unlock(player: Player): Boolean {
        for(lock in locks) {
            if(!lock.check(player)) {
                player.sendMsg(Shining.prefixName, "&c您未达成解锁该元素的条件: ${lock.description(player)}")
                lock.tip(player)
                return false
            }
        }
        
        locks.forEach { 
            if(it.isConsume) {
                it.consume(player)
            }
        }
        
        getPlayerData(player).condition = UNLOCKED
        return true
    }
    
    fun update() {
        symbolHandlers.forEach { 
            it.update()
        }
    }

    fun isPlayerCompleted(player: Player): Boolean =
        teamDataMap[player.uniqueId]?.condition == COMPLETE
    
    fun isPlayerDependencyUnlocked(player: Player): Boolean {
        for(dependency in dependencies) {
            if(!dependency.isPlayerCompleted(player)) {
                return false
            }
        }

        return true
    }
    
    fun isPlayerUnlocked(player: Player): Boolean =
        teamDataMap[player.uniqueId]?.condition?.let { 
            it == UNLOCKED || it == COMPLETE
        } ?: false

    fun hasLock(): Boolean = locks.isNotEmpty()
    
    fun getCondition(player: Player): ElementCondition =
        if(isPlayerCompleted(player)) {
            COMPLETE
        } else if(isPlayerUnlocked(player)) {
            UNLOCKED
        } else if(!isPlayerDependencyUnlocked(player)) {
            LOCKED_DEPENDENCY
        } else if(hasLock()) {
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
                lore += "§a> 请先完成下列元素"
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
                val lore = theSymbol.getLore()
                lore += "&a> 点击解锁"
                lore += ""

                locks.forEach {
                    lore += if(it.isConsume) {
                        "&7需要消耗 ${it.description(player)}"
                    } else {
                        "&7需要 ${it.description(player)}"
                    }
                }

                theSymbol.setLore(lore)
            }
        }
    
    fun getPlayerData(uuid: UUID): ElementPlayerData =
        teamDataMap[uuid] ?: ElementPlayerData().also { 
            teamDataMap[uuid] = it
        }
    
    fun getPlayerData(player: Player): ElementPlayerData =
        getPlayerData(player.uniqueId)
    
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
    
    
    inner class SymbolItemDelegate(val itemBuilder: () -> ItemStack) : Updatable {
        var symbolItem: ItemStack = itemBuilder()
        
        init {
            symbolHandlers += this
        }
        
        operator fun getValue(ref: Any?, property: KProperty<*>): ItemStack = symbolItem
        
        override fun update() {
            symbolItem = itemBuilder()
        }
        
    }
    
}
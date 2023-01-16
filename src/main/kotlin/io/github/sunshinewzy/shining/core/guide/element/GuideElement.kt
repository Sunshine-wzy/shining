package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.*
import io.github.sunshinewzy.shining.core.guide.ElementCondition.*
import io.github.sunshinewzy.shining.core.guide.data.ElementTeamData
import io.github.sunshinewzy.shining.core.lang.getDefaultLangText
import io.github.sunshinewzy.shining.core.lang.getLangText
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
    private var id: NamespacedId,
    private val description: ElementDescription,
    private var symbol: ItemStack
) : IGuideElement {
    private val dependencies: MutableList<IGuideElement> = LinkedList()
    private val locks = LinkedList<ElementLock>()
    private val symbolHandlers = ArrayList<Updatable>()
    
    private val previousElementMap = HashMap<UUID, IGuideElement>()
    private val teamDataMap = HashMap<GuideTeam, ElementTeamData>()

    private val completedSymbol: ItemStack by SymbolItemDelegate {
        val symbolItem = symbol.clone()
        val loreList = LinkedList<String>()
        loreList += getDefaultLangText(COMPLETE_TEXT)
        loreList += ""
        loreList += symbolItem.getLore()
        symbolItem.setLore(loreList)
    }
    private val lockedSymbol: ItemStack by SymbolItemDelegate {
        SItem(
            Material.BARRIER,
            symbol.getMeta().displayName,
            "&7$id"
        )
    }


    override fun getId(): NamespacedId {
        return id
    }

    override fun getName(): String {
        return symbol.getDisplayName(id.toString())
    }

    override fun open(player: Player, team: GuideTeam, previousElement: IGuideElement?) {
        if(previousElement != null)
            previousElementMap[player.uniqueId] = previousElement
        
        ShiningGuide.playerLastOpenElementMap[player.uniqueId] = this
        
        ShiningGuide.soundOpen.playSound(player)    // TODO: Allow every element to customize the open sound
        openMenu(player, team)
    }
    
    protected abstract fun openMenu(player: Player, team: GuideTeam)
    
    override fun back(player: Player, team: GuideTeam) {
        previousElementMap[player.uniqueId]?.let { 
            it.open(player, team, null)
            return
        }

        ShiningGuide.openMainMenu(player)
    }
    
    override fun unlock(player: Player, team: GuideTeam): Boolean {
        for(lock in locks) {
            if(!lock.check(player)) {
                player.sendMsg(Shining.prefix, "${player.getLangText("menu-shining_guide-element-unlock-fail")}: ${lock.description(player)}")
                lock.tip(player)
                return false
            }
        }
        
        locks.forEach { 
            if(it.isConsume) {
                it.consume(player)
            }
        }
        
        getTeamData(team).condition = UNLOCKED
        return true
    }

    override fun update(state: IGuideElementState): Boolean {
        TODO()
    }

    override fun getState(): IGuideElementState {
        TODO()
    }

    fun update() {
        symbolHandlers.forEach { 
            it.update()
        }
    }

    override fun isTeamCompleted(team: GuideTeam): Boolean =
        teamDataMap[team]?.condition == COMPLETE
    
    fun isTeamDependencyUnlocked(team: GuideTeam): Boolean {
        for(dependency in dependencies) {
            if(!dependency.isTeamCompleted(team)) {
                return false
            }
        }

        return true
    }
    
    fun isTeamUnlocked(team: GuideTeam): Boolean =
        teamDataMap[team]?.condition?.let { 
            it == UNLOCKED || it == COMPLETE
        } ?: false

    fun hasLock(): Boolean = locks.isNotEmpty()
    
    override fun getCondition(team: GuideTeam): ElementCondition =
        if(isTeamCompleted(team)) {
            COMPLETE
        } else if(isTeamUnlocked(team)) {
            UNLOCKED
        } else if(!isTeamDependencyUnlocked(team)) {
            LOCKED_DEPENDENCY
        } else if(hasLock()) {
            LOCKED_LOCK
        } else {
            UNLOCKED
        }
    
    override fun getSymbolByCondition(player: Player, team: GuideTeam, condition: ElementCondition): ItemStack =
        when(condition) {
            COMPLETE -> completedSymbol
            
            UNLOCKED -> symbol
            
            LOCKED_DEPENDENCY -> {
                val theSymbol = lockedSymbol.clone()
                val lore = theSymbol.getLore()
                lore += player.getLangText(LOCKED_TEXT)
                lore += ""
                
                lore += player.getLangText("menu-shining_guide-element-symbol-locked_dependency")
                lore += ""
                dependencies.forEach { 
                    if(!it.isTeamCompleted(team)) {
                        lore += it.getName()
                    }
                }
                
                theSymbol.setLore(lore)
            }
            
            LOCKED_LOCK -> {
                val theSymbol = lockedSymbol.clone()
                val lore = theSymbol.getLore()
                lore += player.getLangText(LOCKED_TEXT)
                lore += ""
                
                lore += player.getLangText("menu-shining_guide-element-symbol-locked_lock")
                lore += ""

                locks.forEach {
                    lore += if(it.isConsume) {
                        "${player.getLangText("menu-shining_guide-element-symbol-locked_lock-need_consume")} ${it.description(player)}"
                    } else {
                        "${player.getLangText("menu-shining_guide-element-symbol-locked_lock-need")} ${it.description(player)}"
                    }
                }

                theSymbol.setLore(lore)
            }
        }
    
    fun getTeamData(team: GuideTeam): ElementTeamData =
        teamDataMap.getOrPut(team) { ElementTeamData() }
    
    fun registerDependency(element: IGuideElement) {
        dependencies += element
    }
    
    fun registerLock(lock: ElementLock) {
        locks += lock
    }
    
    
    companion object {
        const val LOCKED_TEXT = "menu-shining_guide-element-text-locked"
        const val COMPLETE_TEXT = "menu-shining_guide-element-text-complete"
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
package io.github.sunshinewzy.shining.core.guide.element

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.ElementCondition
import io.github.sunshinewzy.shining.api.guide.ElementCondition.*
import io.github.sunshinewzy.shining.api.guide.ElementDescription
import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.lock.ElementLock
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.ShiningGuide
import io.github.sunshinewzy.shining.core.guide.data.ElementTeamData
import io.github.sunshinewzy.shining.core.guide.state.GuideElementState
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.utils.sendMsg
import io.github.sunshinewzy.shining.utils.setNameAndLore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

abstract class GuideElement(
    private var id: NamespacedId,
    private var description: ElementDescription,
    private var symbol: ItemStack
) : IGuideElement {
    private val dependencyMap: MutableMap<NamespacedId, IGuideElement> = HashMap()
    private val locks: MutableList<ElementLock> = LinkedList()

    private val previousElementMap: MutableMap<UUID, IGuideElement> = HashMap()
    private val teamDataMap: MutableMap<EntityID<Int>, ElementTeamData> = HashMap()

    
    constructor() : this(NamespacedId.NULL, ElementDescription.NULL, SItem.AIR)


    override fun getId(): NamespacedId = id

    override fun getDescription(): ElementDescription = description

    override fun getSymbol(): ItemStack = symbol

    override fun open(player: Player, team: GuideTeam, previousElement: IGuideElement?, context: GuideContext) {
        if (previousElement != null)
            previousElementMap[player.uniqueId] = previousElement

        ShiningGuide.recordLastOpenElement(player, this)

        ShiningGuide.soundOpen.playSound(player)    // TODO: Allow every element to customize the open sound
        openMenu(player, team, context)
    }

    protected abstract fun openMenu(player: Player, team: GuideTeam, context: GuideContext)

    override fun back(player: Player, team: GuideTeam, context: GuideContext) {
        previousElementMap[player.uniqueId]?.let {
            it.open(player, team, null, context)
            return
        }

        ShiningGuide.openMainMenu(player, team, context)
    }

    override fun unlock(player: Player, team: GuideTeam): Boolean {
        for (lock in locks) {
            if (!lock.check(player)) {
                player.sendMsg(
                    Shining.prefix,
                    "${player.getLangText("menu-shining_guide-element-unlock-fail")}: ${lock.description(player)}"
                )
                lock.tip(player)
                return false
            }
        }

        locks.forEach {
            if (it.isConsume) {
                it.consume(player)
            }
        }

        getTeamData(team).condition = UNLOCKED
        return true
    }

    override fun update(state: IGuideElementState): Boolean {
        if (state !is GuideElementState) return false

        state.id?.let { id = it }
        state.descriptionName?.let { description = ElementDescription(it, ArrayList(state.descriptionLore)) }
        state.symbol?.let { symbol = it.clone() }
        
        dependencyMap.clear()
        state.getDependencyElementMapTo(dependencyMap)
        locks.clear()
        state.locks.mapTo(locks) { it.clone() }

        return true
    }

    override fun saveToState(state: IGuideElementState): Boolean {
        if (state !is GuideElementState) return false

        state.id = id
        state.updateElement()
        state.descriptionName = description.name
        state.descriptionLore.clear()
        state.descriptionLore += description.lore
        state.symbol = symbol.clone()
        
        state.dependencies.clear()
        state.dependencies += dependencyMap.keys
        state.locks.clear()
        locks.mapTo(state.locks) { it.clone() }
        return true
    }

    override fun isTeamCompleted(team: GuideTeam): Boolean =
        team === GuideTeam.CompletedTeam || teamDataMap[team.id]?.condition == COMPLETE

    fun isTeamDependencyCompleted(team: GuideTeam): Boolean {
        for (dependency in dependencyMap.values) {
            if (!dependency.isTeamCompleted(team)) {
                return false
            }
        }

        return true
    }

    fun isTeamUnlocked(team: GuideTeam): Boolean =
        teamDataMap[team.id]?.condition?.let {
            it == UNLOCKED || it == COMPLETE
        } ?: false

    fun hasLock(): Boolean = locks.isNotEmpty()

    override fun getCondition(team: GuideTeam): ElementCondition =
        if (isTeamCompleted(team)) {
            COMPLETE
        } else if (isTeamUnlocked(team)) {
            UNLOCKED
        } else if (!isTeamDependencyCompleted(team)) {
            LOCKED_DEPENDENCY
        } else if (hasLock()) {
            LOCKED_LOCK
        } else {
            UNLOCKED
        }

    override fun getSymbolByCondition(player: Player, team: GuideTeam, condition: ElementCondition): ItemStack =
        when (condition) {
            COMPLETE -> {
                val symbolItem = symbol.clone()
                val loreList = ArrayList<String>()
                loreList += player.getLangText(TEXT_COMPLETE)
                loreList += ""
                loreList += description.lore
                symbolItem.setNameAndLore(description.name, loreList)
            }

            UNLOCKED -> description.setOnItem(symbol.clone())

            LOCKED_DEPENDENCY -> {
                val lore = ArrayList<String>()
                lore += "&7$id"
                lore += player.getLangText(TEXT_LOCKED)
                lore += ""

                lore += player.getLangText("menu-shining_guide-element-symbol-locked_dependency")
                lore += ""

                dependencyMap.values.forEach {
                    if (!it.isTeamCompleted(team)) {
                        lore += it.getDescription().name
                    }
                }

                SItem(Material.BARRIER, description.name, lore)
            }
            
            LOCKED_LOCK -> {
                val lore = ArrayList<String>()
                lore += "&7$id"
                lore += player.getLangText(TEXT_LOCKED)
                lore += ""

                lore += player.getLangText("menu-shining_guide-element-symbol-locked_lock")
                lore += ""

                locks.forEach {
                    lore += if (it.isConsume) {
                        "${player.getLangText("menu-shining_guide-element-symbol-locked_lock-need_consume")} ${it.description(player)}"
                    } else {
                        "${player.getLangText("menu-shining_guide-element-symbol-locked_lock-need")} ${it.description(player)}"
                    }
                }

                SItem(Material.BARRIER, description.name, lore)
            }
        }

    override fun register(): GuideElement {
        return GuideElementRegistry.register(this)
    }

    fun getTeamData(team: GuideTeam): ElementTeamData =
        teamDataMap.getOrPut(team.id) { ElementTeamData() }

    fun registerDependency(element: IGuideElement) {
        dependencyMap[element.getId()] = element
    }

    fun registerLock(lock: ElementLock) {
        locks += lock
    }

    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GuideElement) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
    

    companion object {
        const val TEXT_LOCKED = "menu-shining_guide-element-text-locked"
        const val TEXT_COMPLETE = "menu-shining_guide-element-text-complete"
    }

}
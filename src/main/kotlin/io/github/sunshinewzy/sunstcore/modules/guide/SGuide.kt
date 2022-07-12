package io.github.sunshinewzy.sunstcore.modules.guide

import io.github.sunshinewzy.sunstcore.objects.item.SunSTIcon
import io.github.sunshinewzy.sunstcore.objects.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import java.util.*

object SGuide {
    private val elementMap = TreeMap<Int, MutableList<GuideElement>>()
    
    private val menuBuilder: Linked<GuideElement>.() -> Unit = {
        rows(6)
        slots(slotOrders)
        
        elements { getElements() }

        val lockedElements = LinkedList<GuideElement>()
        onGenerate { player, element, index, slot ->
            val condition = element.getCondition(player)
            if(condition == ElementCondition.LOCKED_DEPENDENCY || condition == ElementCondition.LOCKED_LOCK)
                lockedElements += element
            element.getSymbolByCondition(player, condition)
        }

        onBuild { inv ->
            edgeOrders.forEach { index ->
                if(inv.getItem(index)?.type != Material.AIR) return@forEach
                inv.setItem(index, SunSTIcon.EDGE.item)
            }
        }

        setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
            if(hasPreviousPage) {
                SunSTIcon.PAGE_PRE_GLASS_PANE.item
            } else SunSTIcon.EDGE.item
        }

        setNextPage(8 orderWith 6) { page, hasNextPage ->
            if(hasNextPage) {
                SunSTIcon.PAGE_NEXT_GLASS_PANE.item
            } else SunSTIcon.EDGE.item
        }

        onClick { event, element ->
            if(element in lockedElements) return@onClick
            
            element.open(event.clicker, element)
        }
    }

    
    const val TITLE = "SunSTCore Guide"
    
    
    val edgeOrders = (((1 orderWith 1)..(9 orderWith 1)) + ((1 orderWith 6)..(9 orderWith 6)))
//    val edgeOrders = (((1 orderWith 1)..(9 orderWith 1)) + ((1 orderWith 6)..(9 orderWith 6))) - (2 orderWith 6) - (8 orderWith 1) - (8 orderWith 6)
//    val edgeOrders = ((1 orderWith 1)..(9 orderWith 1)) + (1 orderWith 6) + ((3 orderWith 6)..(7 orderWith 6)) + (9 orderWith 6)
    val slotOrders = ((1 orderWith 2)..(9 orderWith 5)).toList()
    val playerLastOpenElementMap = HashMap<UUID, GuideElement>()
    
    
    fun open(player: Player) {
        playerLastOpenElementMap -= player.uniqueId
        
        player.openMenu(TITLE, menuBuilder)
    }
    
    fun openLastElement(player: Player) {
        playerLastOpenElementMap[player.uniqueId]?.let {
            it.open(player, null)
            return
        }
        
        open(player)
    }
    
    fun registerElement(element: GuideElement, priority: Int = 10) {
        elementMap[priority]?.let { 
            it += element
            return
        }
        
        val list = LinkedList<GuideElement>()
        list += element
        elementMap[priority] = list
    }
    
    
    private fun getElements(): List<GuideElement> {
        val list = LinkedList<GuideElement>()
        elementMap.values.forEach { 
            list += it
        }
        return list
    }
    
}
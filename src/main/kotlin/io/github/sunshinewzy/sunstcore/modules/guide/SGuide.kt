package io.github.sunshinewzy.sunstcore.modules.guide

import io.github.sunshinewzy.sunstcore.objects.item.GuideIcon
import io.github.sunshinewzy.sunstcore.objects.orderWith
import io.github.sunshinewzy.sunstcore.utils.sendMsg
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import java.util.*

object SGuide {
    private val elementMap = TreeMap<Int, MutableList<GuideElement>>()
    private val edgeOrders = ((1 orderWith 1)..(9 orderWith 1)) + (1 orderWith 6) + ((3 orderWith 6)..(7 orderWith 6)) + (9 orderWith 6)
    private val slotOrders = ((1 orderWith 2)..(9 orderWith 5)).toList()
    
    
    fun open(player: Player) {
        player.openMenu<Linked<GuideElement>>("SunSTCore Guide") { 
            
            rows(6)
            slots(slotOrders)
            
            elements { getElements() }
            
            onGenerate { player, element, index, slot -> 
                element.symbol
            }
            
            onBuild { inv ->
                edgeOrders.forEach { 
                    inv.setItem(it, GuideIcon.EDGE.item)
                }
            }
            
            setPreviousPage(2 orderWith 6) { page, hasPreviousPage ->
                if(hasPreviousPage) {
                    GuideIcon.PAGE_PRE_GLASS_PANE.item
                } else GuideIcon.EDGE.item
            }
            
            setNextPage(8 orderWith 6) { page, hasNextPage -> 
                if(hasNextPage) {
                    GuideIcon.PAGE_NEXT_GLASS_PANE.item
                } else GuideIcon.EDGE.item
            }
            
            onClick { event, element ->
                element.open(event.clicker, element)
            }
            
        }
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
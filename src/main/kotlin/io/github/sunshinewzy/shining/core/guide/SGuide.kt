package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.dictionary.DictionaryItem
import io.github.sunshinewzy.shining.core.dictionary.DictionaryRegistry
import io.github.sunshinewzy.shining.core.dictionary.item.behavior.ItemBehavior
import io.github.sunshinewzy.shining.core.guide.GuideGroup.Companion.getGuideGroup
import io.github.sunshinewzy.shining.core.guide.GuideGroup.Companion.setupGuideGroup
import io.github.sunshinewzy.shining.objects.SCollection
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.objects.item.SunSTIcon
import io.github.sunshinewzy.shining.objects.orderWith
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.ClickEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.isAir
import java.util.*

object SGuide {
    private val elementMap = TreeMap<Int, MutableList<GuideElement>>()
    private val guideItem: DictionaryItem = DictionaryRegistry.registerItem(
        NamespacedId(Shining, "SGUIDE"),
        SItem(Material.ENCHANTED_BOOK, "&aSGuide"),
        object : ItemBehavior() {
            override fun onInteract(event: PlayerInteractEvent, player: Player, item: ItemStack, action: Action) {
                if(event.hand != EquipmentSlot.HAND) return
                
                if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    event.isCancelled = true
                    openLastElement(player)
                } else if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    event.isCancelled = true
                    open(player)
                }
            }
        }
    )
    
    
    val onBuildEdge: (Inventory) -> Unit = { inv ->
        edgeOrders.forEach { index ->
            inv.getItem(index)?.let { 
                if(!it.isAir()) return@forEach
            }
            
            inv.setItem(index, SunSTIcon.EDGE.item)
        }
    }
    val onClickBack: (ClickEvent) -> Unit = {
        if(it.clickEvent().isShiftClick) {
            open(it.clicker)
        } else {
            openLastElement(it.clicker)
        }
    }

    
    const val TITLE = "Shining Guide"
    
    
    val edgeOrders = (((1 orderWith 1)..(9 orderWith 1)) + ((1 orderWith 6)..(9 orderWith 6)))
    val slotOrders = ((1 orderWith 2)..(9 orderWith 5)).toList()
    val playerLastOpenElementMap = HashMap<UUID, GuideElement>()
    
    
    fun open(player: Player) {
        playerLastOpenElementMap -= player.uniqueId
        
        val group = player.getGuideGroup() ?: kotlin.run { 
            player.setupGuideGroup()
            return
        }
        
        player.openMenu<Linked<GuideElement>>(TITLE) {
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

            onBuild(true, onBuildEdge)

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

                element.open(event.clicker, null)
            }
        }
    }
    
    fun openLastElement(player: Player) {
        playerLastOpenElementMap[player.uniqueId]?.let {
            it.open(player)
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
    
    fun fireworkCongratulate(player: Player) {
        val firework = player.world.spawnEntity(player.location, EntityType.FIREWORK) as Firework
        val meta = firework.fireworkMeta
        meta.addEffect(FireworkEffect.builder().with(SCollection.fireworkEffectTypes.random()).withColor(SCollection.colors.random()).build())
        meta.power = 1
        firework.fireworkMeta = meta
    }
    
    fun getItem(): ItemStack {
        return guideItem.item.clone()
    }
    
    
    private fun getElements(): List<GuideElement> {
        val list = LinkedList<GuideElement>()
        elementMap.values.forEach { 
            list += it
        }
        return list
    }
    
}
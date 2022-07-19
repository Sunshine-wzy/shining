package io.github.sunshinewzy.sunstcore.modules.guide.element

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.modules.guide.ElementCondition
import io.github.sunshinewzy.sunstcore.modules.guide.GuideElement
import io.github.sunshinewzy.sunstcore.modules.guide.SGuide
import io.github.sunshinewzy.sunstcore.objects.item.SunSTIcon
import io.github.sunshinewzy.sunstcore.objects.orderWith
import io.github.sunshinewzy.sunstcore.objects.SCollection
import io.github.sunshinewzy.sunstcore.utils.sendMsg
import org.bukkit.FireworkEffect
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import java.util.*

class GuideCategory(id: String, symbol: ItemStack) : GuideElement(id, symbol) {
    private val elements = LinkedList<GuideElement>()
    
    
    override fun openAction(player: Player) {
        player.openMenu<Linked<GuideElement>>(SGuide.TITLE) {
            rows(6)
            slots(SGuide.slotOrders)

            elements { elements }

            val lockedElements = LinkedList<GuideElement>()
            onGenerate { player, element, index, slot ->
                val condition = element.getCondition(player)
                if(condition == ElementCondition.LOCKED_DEPENDENCY || condition == ElementCondition.LOCKED_LOCK)
                    lockedElements += element
                element.getSymbolByCondition(player, condition)
            }

            onBuild(onBuild = SGuide.onBuildEdge)

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
                if(element in lockedElements) {
                    if(element.unlock(player)) {
                        val firework = player.world.spawnEntity(player.location, EntityType.FIREWORK) as Firework
                        val meta = firework.fireworkMeta
                        meta.addEffect(FireworkEffect.builder().with(SCollection.fireworkEffectTypes.random()).withColor(SCollection.colors.random()).build())
                        firework.fireworkMeta = meta
                        open(player)
                    } else {
                        player.sendMsg(SunSTCore.prefixName, "&c您未达成解锁该元素的所有条件")
                    }
                    
                    return@onClick
                }

                element.open(event.clicker, this@GuideCategory)
            }

            set(2 orderWith 1, SunSTIcon.BACK.item) {
                if(clickEvent().isShiftClick) {
                    SGuide.open(clicker)
                } else {
                    back(clicker)
                }
            }
        }
    }
    
    
    fun registerElement(element: GuideElement) {
        elements += element
    }
    
}
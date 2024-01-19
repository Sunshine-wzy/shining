package io.github.sunshinewzy.shining.core.guide

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.team.IGuideTeam
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import io.github.sunshinewzy.shining.core.guide.element.GuideElementRegistry
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.item.NamespacedIdItem
import io.github.sunshinewzy.shining.core.menu.onBack
import io.github.sunshinewzy.shining.core.menu.onBackMenu
import io.github.sunshinewzy.shining.core.menu.openDeleteConfirmMenu
import io.github.sunshinewzy.shining.core.menu.openMultiPageMenu
import io.github.sunshinewzy.shining.objects.ShiningDispatchers
import io.github.sunshinewzy.shining.objects.item.ShiningIcon
import io.github.sunshinewzy.shining.utils.orderWith
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import java.util.*

object ShiningGuideRecycleBin {
    
    private val cache: MutableMap<NamespacedId, IGuideElement> = TreeMap()
    private var init: Boolean = false
    
    private val itemOpenElement = NamespacedIdItem(Material.REDSTONE_LAMP, NamespacedId(Shining, "shining_guide-settings-recycle_bin-open_element"))
    
    
    fun refresh() {
        init = true
        cache.clear()
        GuideElementRegistry.getLostElementsTo(cache)
    }
    
    fun open(player: Player, team: IGuideTeam) {
        if (!init) refresh()
        
        player.openMultiPageMenu<IGuideElement>(player.getLangText("menu-shining_guide-recycle_bin-title")) { 
            elements { cache.map { it.value } }
            
            onGenerate(async = true) { player, element, _, _ ->
                element.getUnlockedSymbolFuture(player).get()
            }
            
            onClick { event, element -> 
                editElement(player, team, element)
            }
            
            set(8 orderWith 1, ShiningIcon.REFRESH.toLocalizedItem(player)) {
                refresh()
                open(player, team)
            }

            onBackMenu(player, team, slot = 2 orderWith 1)
        }
    }
    
    fun editElement(player: Player, team: IGuideTeam, element: IGuideElement) {
        player.openMenu<Basic>(player.getLangText("menu-shining_guide-recycle_bin-title")) { 
            rows(3)
            
            map(
                "-B-------",
                "- a c d -",
                "---------"
            )
            
            set('-', ShiningIcon.EDGE.item)
            
            set('a', itemOpenElement.toLocalizedItem(player)) {
                element.open(player, team)
            }
            
            set('c', ShiningIcon.CUT.toLocalizedItem(player)) {
                ShiningGuideClipboard.copy(player, GuideClipboardSession(
                    element,
                    null,
                    GuideClipboardSession.Mode.CUT
                ) { cache -= it.element.getId() })
                ShiningGuide.openLastElement(player, team)
            }
            
            set('d', ShiningIcon.REMOVE.toLocalizedItem(player)) {
                player.openDeleteConfirmMenu { 
                    onConfirm { 
                        cache -= element.getId()
                        ShiningDispatchers.transactionIO { 
                            GuideElementRegistry.removeElement(element)
                        }
                    }
                    
                    onFinal { open(player, team) }
                }
            }
            
            onBack(player) { open(player, team) }
            
            onClick(lock = true)
        }
    }
    
}
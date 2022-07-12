package io.github.sunshinewzy.sunstcore.objects.menu

import io.github.sunshinewzy.sunstcore.SunSTCore.colorName
import io.github.sunshinewzy.sunstcore.SunSTCore.prefixName
import io.github.sunshinewzy.sunstcore.listeners.ChatListener
import io.github.sunshinewzy.sunstcore.modules.guide.SGuide
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.getLore
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.setName
import io.github.sunshinewzy.sunstcore.objects.SMenu
import io.github.sunshinewzy.sunstcore.objects.item.SunSTIcon
import io.github.sunshinewzy.sunstcore.objects.orderWith
import io.github.sunshinewzy.sunstcore.utils.*
import org.bukkit.Material
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import java.util.*

object SunSTMenu {
    
    val itemEditor: SBuilder<Basic> = {
        rows(3)
        
        map(
            "oooxxxxxx",
            "o oxaxxbx",
            "oooxxxxxx"
        )
        
        set('o', SunSTIcon.EDGE_GLASS_PANE.item)
        set('x', SunSTIcon.EDGE.item)
        set('a', SunSTIcon.RENAME.item)
        set('b', SunSTIcon.EDIT_LORE.item)
        
        val editItemOrder = 2 orderWith 2
        
        onBuild { player, inventory -> 
            inventory.setItem(editItemOrder, player.inventory.itemInMainHand)
        }
        
        onClick('a') { event ->
            val player = event.clicker
            val item = event.inventory.getItem(editItemOrder) ?: return@onClick
            
            ChatListener.registerPlayerChatSubscriber(PlayerChatSubscriber(player) {
                if(message == ".") {
                    player.sendMsg(prefixName, "&e物品名称编辑已取消")
                } else {
                    item.setName(message)
                    player.sendMsg(prefixName, "&a物品名称编辑成功")
                }
                
                true
            })
            
            player.sendMessage("§f[$prefixName§f] §a请输入新的物品名称§f(§e可用'§a&§e'表示颜色§f, §c输入'§a.§c'以取消§f)")
            player.closeInventory()
        }
        
        onClick('b') { event ->
            val item = event.inventory.getItem(editItemOrder) ?: return@onClick
            
            event.clicker.openMenu<Linked<String>>("编辑 Lore") {
                rows(6)
                slots(SGuide.slotOrders)

                elements { item.getLore() }

                var iterator = wools.iterator()
                onGenerate { player, element, index, slot ->
                    if(!iterator.hasNext()) iterator = wools.iterator()
                    SItem(iterator.next(), "&f$index", element)
                }

                onBuild { inv ->
                    SGuide.edgeOrders.forEach { index ->
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
                    
                }
            }
        }
    }
    
    val itemEditorOld = SMenu("ItemEdit", "物品编辑", 1)
        .setButton(3, 1, SItem(Material.NAME_TAG, "&e重命名"), "Rename") {
            val player = getPlayer()
            itemEditNamePlayers += player.uniqueId
            player.sendMessage("§f[$colorName§f] §a请输入新的物品名称§f(§e可用'§a&§e'表示颜色§f, §c输入'§a.§c'以取消§f)")
            player.closeInventory()
        }
        .setButton(7, 1, SItem(Material.EMERALD, "&a编辑Lore"), "EditLore")
    
    
    val wools = arrayListOf(Material.LIME_WOOL, Material.YELLOW_WOOL, Material.LIGHT_BLUE_WOOL, Material.PINK_WOOL, Material.ORANGE_WOOL, Material.WHITE_WOOL, Material.MAGENTA_WOOL, Material.CYAN_WOOL, Material.LIGHT_GRAY_WOOL, Material.PURPLE_WOOL, Material.BROWN_WOOL, Material.BLUE_WOOL, Material.GREEN_WOOL, Material.GRAY_WOOL, Material.RED_WOOL, Material.BLACK_WOOL)
    
    
    private val itemEditNamePlayers = HashSet<UUID>()
    
    
    init {
        subscribeEvent<AsyncPlayerChatEvent> { 
            if(player.uniqueId in itemEditNamePlayers) {
                if(message == ".") {
                    player.sendMsg(colorName, "&c物品名称编辑已取消")
                } else {
                    player.inventory.itemInMainHand.setName(message)
                    player.sendMsg(colorName, "&a物品名称成功设置为: &f$message")
                }
                
                isCancelled = true
                itemEditNamePlayers -= player.uniqueId
            }
        }
    }
    
}
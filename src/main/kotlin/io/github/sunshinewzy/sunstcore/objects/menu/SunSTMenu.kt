package io.github.sunshinewzy.sunstcore.objects.menu

import io.github.sunshinewzy.sunstcore.SunSTCore.colorName
import io.github.sunshinewzy.sunstcore.core.menu.SMenu
import io.github.sunshinewzy.sunstcore.objects.SItem
import io.github.sunshinewzy.sunstcore.objects.SItem.Companion.setName
import io.github.sunshinewzy.sunstcore.utils.getPlayer
import io.github.sunshinewzy.sunstcore.utils.sendMsg
import io.github.sunshinewzy.sunstcore.utils.subscribeEvent
import org.bukkit.Material
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

object SunSTMenu {
    
    val itemEditor = SMenu("ItemEdit", "物品编辑", 1)
        .setButton(3, 1, SItem(Material.NAME_TAG, "&e重命名"), "Rename") {
            val player = getPlayer()
            itemEditNamePlayers += player.uniqueId
            player.sendMessage("§f[$colorName§f] §a请输入新的物品名称§f(§e可用'§a&§e'表示颜色§f, §c输入'§a.§c'以取消§f)")
            player.closeInventory()
        }
        .setButton(7, 1, SItem(Material.EMERALD, "&a编辑Lore"), "EditLore")
    
    
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
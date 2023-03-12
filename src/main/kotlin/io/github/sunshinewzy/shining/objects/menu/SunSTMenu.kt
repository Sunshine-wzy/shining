package io.github.sunshinewzy.shining.objects.menu

import io.github.sunshinewzy.shining.Shining.COLOR_NAME
import io.github.sunshinewzy.shining.core.menu.SMenu
import io.github.sunshinewzy.shining.objects.SItem
import io.github.sunshinewzy.shining.utils.getPlayer
import io.github.sunshinewzy.shining.utils.sendMsg
import io.github.sunshinewzy.shining.utils.setName
import io.github.sunshinewzy.shining.utils.subscribeEvent
import org.bukkit.Material
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

object SunSTMenu {

    val itemEditor = SMenu("ItemEdit", "物品编辑", 1)
        .setButton(3, 1, SItem(Material.NAME_TAG, "&e重命名"), "Rename") {
            val player = getPlayer()
            itemEditNamePlayers += player.uniqueId
            player.sendMessage("§f[$COLOR_NAME§f] §a请输入新的物品名称§f(§e可用'§a&§e'表示颜色§f, §c输入'§a.§c'以取消§f)")
            player.closeInventory()
        }
        .setButton(7, 1, SItem(Material.EMERALD, "&a编辑Lore"), "EditLore")


    private val itemEditNamePlayers = HashSet<UUID>()


    init {
        subscribeEvent<AsyncPlayerChatEvent> {
            if (player.uniqueId in itemEditNamePlayers) {
                if (message == ".") {
                    player.sendMsg(COLOR_NAME, "&c物品名称编辑已取消")
                } else {
                    player.inventory.itemInMainHand.setName(message)
                    player.sendMsg(COLOR_NAME, "&a物品名称成功设置为: &f$message")
                }

                isCancelled = true
                itemEditNamePlayers -= player.uniqueId
            }
        }
    }

}
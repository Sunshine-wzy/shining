package io.github.sunshinewzy.shining.commands

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.utils.ItemEditor
import io.github.sunshinewzy.shining.utils.sendMsg
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

internal object CommandItem {

    val item = subCommand {
        execute<Player> { sender, context, argument ->
            val item = sender.inventory.itemInMainHand
            if (item.type != Material.AIR) {
                ItemEditor.editItem(item, sender)
            } else sender.sendMsg(Shining.prefix, "&c手持物品不能为空")
        }
    }

}
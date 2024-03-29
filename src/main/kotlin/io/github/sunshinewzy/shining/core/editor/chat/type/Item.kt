package io.github.sunshinewzy.shining.core.editor.chat.type

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditorSession
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.objects.SItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.ItemStack
import taboolib.module.chat.ComponentText
import taboolib.module.chat.colored
import taboolib.module.nms.getName
import taboolib.platform.util.isAir

class Item(name: String) : ChatEditorSession<ItemStack>(name) {
    override var content: ItemStack = SItem(Material.AIR)


    override fun display(player: Player, component: ComponentText) {
        component.append(
                if (isCorrect) player.getLangText("text-editor-chat-content_correct", "${content.getName(player)} x${content.amount}").colored()
                else player.getLangText("text-editor-chat-content_incorrect", "${content.getName(player)} x${content.amount}").colored()
            )
            .append("    ")
            .append("§7[§b#§7]")
            .hoverText(player.getLangText("text-editor-chat-session-item-input").colored())
            .clickRunCommand("/shiningapi editor chat update")
            .newLine()
            .append("§7|")
    }

    override fun update(event: AsyncPlayerChatEvent) {
        val mainHandItem = event.player.inventory.itemInMainHand
        if (mainHandItem.isAir()) return
        
        content = mainHandItem
        isCorrect = true
    }


    fun item(item: ItemStack?) {
        content = item ?: return
        isCorrect = true
    }
}
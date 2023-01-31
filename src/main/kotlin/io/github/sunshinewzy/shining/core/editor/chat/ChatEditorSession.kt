package io.github.sunshinewzy.shining.core.editor.chat

import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

class ChatEditorSession(
    val player: Player,
    val name: String = "",
    val isInvisible: Boolean = true,
    val action: AsyncPlayerChatEvent.(String) -> Boolean = { true },
    val callback: (content: String) -> Unit
) {
    var content: String = ""
    var isCorrect: Boolean = false
    
    
    fun send(player: Player) {
        TellrawJson().newLine()
            .append(player.getLangText("text-editor-chat-edit", name).colored())
            .newLine()
            .append(
                if(isCorrect) player.getLangText("text-editor-chat-content_correct", content).colored()
                else player.getLangText("text-editor-chat-content_incorrect", content).colored()
            )
            .newLine()
            .append("     §7[§a√§7]")
            .runCommand("/shiningapi editor chat submit")
            .append("         §7[§c×§7]")
            .runCommand("/shiningapi editor chat cancel")
            .newLine()
            .sendTo(adaptPlayer(player))
    }
    
}
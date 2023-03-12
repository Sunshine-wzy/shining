package io.github.sunshinewzy.shining.core.editor.chat.type

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditorSession
import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

open class Text(name: String) : ChatEditorSession<String>(name) {
    override var content: String = ""


    override fun display(player: Player, json: TellrawJson) {
        json.append(
            if (isCorrect) player.getLangText("text-editor-chat-content_correct", content).colored()
            else player.getLangText("text-editor-chat-content_incorrect", content).colored()
        )
            .append("    ")
            .append("§7[§b#§7]")
            .hoverText(player.getLangText("text-editor-chat-session-text_list-input").colored())
            .suggestCommand(content)
            .newLine()
            .append("§7|")
    }

    override fun update(event: AsyncPlayerChatEvent) {
        content = event.message
        isCorrect = true
    }


    fun text(text: String?) {
        content = text ?: return
        isCorrect = true
    }

}
package io.github.sunshinewzy.shining.core.editor.chat.type

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditorSession
import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

class Text(name: String) : ChatEditorSession(name) {

    var content: String = ""
        internal set
    
    
    override fun display(json: TellrawJson, player: Player): TellrawJson =
        json.append(
                if(isCorrect) player.getLangText("text-editor-chat-content_correct", content).colored()
                else player.getLangText("text-editor-chat-content_incorrect", content).colored()
            )

    override fun submit(player: Player) {
        TODO("Not yet implemented")
    }

    override fun update(event: AsyncPlayerChatEvent) {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean = content.isEmpty()
    
}
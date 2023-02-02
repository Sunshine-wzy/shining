package io.github.sunshinewzy.shining.core.editor.chat.type

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditorSession
import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

open class Text(name: String) : ChatEditorSession(name) {
    var content: String = ""
        private set
    var submitCallback: (content: String) -> Unit = {}
        private set
    var cancelCallback: (content: String) -> Unit = {}
        private set
    var finalCallback: (content: String) -> Unit = {}
        private set
    
    
    override fun display(player: Player, json: TellrawJson) {
        json.append(
            if(isCorrect) player.getLangText("text-editor-chat-content_correct", content).colored()
            else player.getLangText("text-editor-chat-content_incorrect", content).colored()
        ).newLine().append("§7|")
    }

    override fun submit(player: Player) {
        submitCallback(content)
    }

    override fun cancel(player: Player) {
        cancelCallback(content)
    }

    override fun final(player: Player) {
        finalCallback(content)
    }

    override fun update(event: AsyncPlayerChatEvent) {
        content = event.message
    }

    override fun isEmpty(): Boolean = content.isEmpty()
    
    
    fun onSubmit(block: (content: String) -> Unit) {
        submitCallback = block
    }
    
    fun onCancel(block: (content: String) -> Unit) {
        cancelCallback = block
    }
    
    fun onFinal(block: (content: String) -> Unit) {
        finalCallback = block
    }
    
    fun content(content: String?) {
        this.content = content ?: return
        this.isCorrect = true
    }
    
}
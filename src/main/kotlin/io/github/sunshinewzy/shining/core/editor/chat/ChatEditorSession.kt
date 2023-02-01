package io.github.sunshinewzy.shining.core.editor.chat

import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

abstract class ChatEditorSession(val name: String) {
    var isInvisible: Boolean = true
        private set
    var predicate: AsyncPlayerChatEvent.(String) -> Boolean = { true }
        private set
//    var callback: (content: T) -> Unit = {}
    
    var isCorrect: Boolean = false
        internal set
    
    
    open fun send(player: Player) {
        TellrawJson().newLine()
            .append(player.getLangText("text-editor-chat-edit", name).colored())
            .newLine()
            .let { display(it, player) }
            .newLine()
            .append("     §7[§a√§7]")
            .runCommand("/shiningapi editor chat submit")
            .append("         §7[§c×§7]")
            .runCommand("/shiningapi editor chat cancel")
            .newLine()
            .sendTo(adaptPlayer(player))
    }
    
    abstract fun display(json: TellrawJson, player: Player): TellrawJson
    
    abstract fun submit(player: Player)
    
    abstract fun update(event: AsyncPlayerChatEvent)
    
    abstract fun isEmpty(): Boolean
    
    
    fun visible() {
        isInvisible = false
    }
    
    fun predicate(block: AsyncPlayerChatEvent.(String) -> Boolean) {
        predicate = block
    }
    
}
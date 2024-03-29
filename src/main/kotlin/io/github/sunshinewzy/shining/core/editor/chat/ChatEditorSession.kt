package io.github.sunshinewzy.shining.core.editor.chat

import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.chat.colored

abstract class ChatEditorSession<T>(val name: String) {
    abstract var content: T
        protected set

    var isCorrect: Boolean = false
        protected set

    private var submitCallback: (content: T) -> Unit = {}
    private var cancelCallback: (content: T) -> Unit = {}
    private var finalCallback: (content: T) -> Unit = {}

    var isInvisible: Boolean = true
        private set
    var predicate: AsyncPlayerChatEvent.(String) -> Boolean = { true }
        private set


    abstract fun display(player: Player, component: ComponentText)

    abstract fun update(event: AsyncPlayerChatEvent)


    open fun send(player: Player) {
        Components.empty().newLine()
            .append(player.getLangText("text-editor-chat-edit", name).colored())
            .newLine()
            .append("§7|")
            .newLine()
            .also { display(player, it) }
            .newLine()
            .append("       ")
            .append(if (isCorrect) "§7[§a√§7]" else "§7[√]")
            .hoverText(player.getLangText("text-editor-chat-session-button-submit").colored())
            .clickRunCommand("/shiningapi editor chat submit")
            .append("       ")
            .append("§7[§c×§7]")
            .hoverText(player.getLangText("text-editor-chat-session-button-cancel").colored())
            .clickRunCommand("/shiningapi editor chat cancel")
            .newLine()
            .sendTo(adaptPlayer(player))
    }

    open fun submit(player: Player) {
        submitCallback(content)
    }

    open fun cancel(player: Player) {
        cancelCallback(content)
    }

    open fun final(player: Player) {
        finalCallback(content)
    }

    open fun onSubmit(block: (content: T) -> Unit) {
        submitCallback = block
    }

    open fun onCancel(block: (content: T) -> Unit) {
        cancelCallback = block
    }

    open fun onFinal(block: (content: T) -> Unit) {
        finalCallback = block
    }

    open fun mode(player: Player, mode: String) {}


    fun visible() {
        isInvisible = false
    }

    fun predicate(block: AsyncPlayerChatEvent.(String) -> Boolean) {
        predicate = block
    }

}
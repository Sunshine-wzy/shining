package io.github.sunshinewzy.shining.core.editor.chat.type

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditorSession
import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.module.chat.ComponentText
import taboolib.module.chat.colored

open class TextMap(name: String) : ChatEditorSession<MutableMap<String, String>>(name) {
    override var content: MutableMap<String, String> = HashMap()

    var index: String = ""
        private set


    override fun display(player: Player, component: ComponentText) {
        var flag = true
        content.forEach { (key, value) ->
            component
                .append(
                    if (key == index) {
                        flag = false
                        "§7| §d$key §d: §f$value"
                    } else "§7| $key §7: §f$value"
                )
                .hoverText(player.getLangText("text-editor-chat-session-text_list-edit").colored())
                .clickRunCommand("/shiningapi editor chat mode $key")
                .append("    ")
                .append("§7[§b#§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-input").colored())
                .clickSuggestCommand(value)
                .newLine()
        }
        
        component
            .append(
                if (flag) "§7| §d[§e○§d]"
                else "§7| [§e○§7]"
            )
            .hoverText(player.getLangText("text-editor-chat-session-text_list-reset").colored())
            .clickRunCommand("/shiningapi editor chat mode __RESET__")
            .newLine()
    }

    override fun update(event: AsyncPlayerChatEvent) {
        if (index.isEmpty()) return
        if (content.containsKey(index)) {
            content[index] = event.message
        }

        checkCorrect()
    }

    override fun mode(player: Player, mode: String) {
        index = if (mode == "__RESET__") ""
        else if (content.containsKey(mode)) mode
        else return
        send(player)
    }


    fun map(map: Map<String, String>) {
        content += map
        checkCorrect()
    }

    fun map(vararg key: String) {
        key.forEach {
            content[it] = ""
        }
        isCorrect = false
    }

    fun checkCorrect() {
        for (value in content.values) {
            if (value.isEmpty()) {
                isCorrect = false
                return
            }
        }
        isCorrect = true
    }

}
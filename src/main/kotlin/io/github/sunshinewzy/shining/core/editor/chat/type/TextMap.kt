package io.github.sunshinewzy.shining.core.editor.chat.type

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditorSession
import io.github.sunshinewzy.shining.core.lang.getLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

open class TextMap(name: String) : ChatEditorSession<MutableMap<String, String>>(name) {
    override var content: MutableMap<String, String> = HashMap()

    var index: String = ""
        private set
    
    
    override fun display(player: Player, json: TellrawJson) {
        content.forEach { (key, value) -> 
            json.append(if(key == index) "§7| §d$key §d: §f$value" else "§7| $key §7: §f$value")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-edit").colored())
                .runCommand("/shiningapi editor chat mode $key")
                .append("    ")
                .append("§7[§b#§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-input").colored())
                .suggestCommand(value)
                .newLine()
        }
    }

    override fun update(event: AsyncPlayerChatEvent) {
        if(content.containsKey(index)) {
            content[index] = event.message
        }
        
        checkCorrect()
    }

    override fun mode(player: Player, mode: String) {
        if(content.containsKey(mode)) {
            index = mode
            send(player)
        }
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

    
    private fun checkCorrect() {
        for(value in content.values) {
            if(value.isEmpty()) {
                isCorrect = false
                return
            }
        }
        isCorrect = true
    }
    
}
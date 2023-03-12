package io.github.sunshinewzy.shining.core.editor.chat.type

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditorSession
import io.github.sunshinewzy.shining.core.editor.chat.type.TextList.Mode.*
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import java.util.*

open class TextList(name: String) : ChatEditorSession<MutableList<String>>(name) {
    override var content: MutableList<String> = LinkedList()

    var mode: Mode = ADD
        private set
    var index: Int = 0
        private set


    override fun display(player: Player, json: TellrawJson) {
        content.forEachIndexed { index, str ->
            json.append(if (mode == EDIT && index == this.index) "§7| §d${index + 1}. §f${str.colored()}" else "§7| ${index + 1}. §f${str.colored()}")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-edit").colored())
                .runCommand("/shiningapi editor chat mode EDIT.$index")
                .append("    ")
                .append("§7[§b#§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-input").colored())
                .suggestCommand(str)
                .append(" ")
                .append(if (mode == ADD && index == this.index) "§d[§a+§d]" else "§7[§a+§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-add").colored())
                .runCommand("/shiningapi editor chat mode ADD.$index")
                .append(" ")
                .append("§7[§c-§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-remove").colored())
                .runCommand("/shiningapi editor chat mode REMOVE.$index")
                .newLine()
        }

        json.append(if (mode == ADD && this.index == content.size) "§7| §d[§a+§d]" else "§7| [§a+§7]")
            .hoverText(player.getLangText("text-editor-chat-session-text_list-add").colored())
            .runCommand("/shiningapi editor chat mode ADD.${content.size}")
    }

    override fun update(event: AsyncPlayerChatEvent) {
        when (mode) {
            EDIT -> {
                if (index in content.indices) {
                    content[index] = event.message
                } else {
                    event.player.sendPrefixedLangText("text-editor-chat-session-text_list-index_out_of_bounds")
                }
            }

            ADD -> {
                if (index in content.indices) {
                    content.add(index, event.message)
                    index++
                } else {
                    content.add(event.message)
                    index = content.size
                }
            }

            REMOVE -> {}
        }

        isCorrect = content.isNotEmpty()
    }

    override fun mode(player: Player, mode: String) {
        val split = mode.split('.')
        if (split.size < 2) return

        val theMode = Mode.fromString(split[0]) ?: return
        val theIndex = split[1].toIntOrNull() ?: return

        if (theMode == REMOVE) {
            if (theIndex in content.indices) {
                content.removeAt(theIndex)
                send(player)
            } else {
                player.sendPrefixedLangText("text-editor-chat-session-text_list-index_out_of_bounds")
            }
            return
        }

        this.mode = theMode
        this.index = theIndex
        send(player)
    }


    fun list(list: List<String>) {
        if (list.isEmpty()) return

        content += list
        index = content.size
        isCorrect = true
    }

    fun getList(): List<String> = content


    enum class Mode {
        EDIT,
        ADD,
        REMOVE;

        companion object {
            fun fromString(mode: String): Mode? {
                return when (mode.uppercase()) {
                    "EDIT" -> EDIT
                    "ADD" -> ADD
                    "REMOVE" -> REMOVE
                    else -> null
                }
            }
        }
    }

}
package io.github.sunshinewzy.shining.core.editor.chat.type

import io.github.sunshinewzy.shining.core.editor.chat.ChatEditorSession
import io.github.sunshinewzy.shining.core.editor.chat.type.TextList.Mode.*
import io.github.sunshinewzy.shining.core.lang.getLangText
import io.github.sunshinewzy.shining.core.lang.sendPrefixedLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.module.chat.ComponentText
import taboolib.module.chat.colored
import java.util.*

open class TextList(name: String) : ChatEditorSession<MutableList<String>>(name) {
    override var content: MutableList<String> = LinkedList()

    var mode: Mode = ADD
        private set
    var index: Int = 0
        private set


    override fun display(player: Player, component: ComponentText) {
        content.forEachIndexed { index, str ->
            component.append(if (mode == EDIT && index == this.index) "§7| §d${index + 1}. §f${str.colored()}" else "§7| ${index + 1}. §f${str.colored()}")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-edit").colored())
                .clickRunCommand("/shiningapi editor chat mode EDIT.$index")
                .append("    ")
                .append("§7[§b#§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-input").colored())
                .clickSuggestCommand(str)
                .append(" ")
                .append(if (mode == ADD && index == this.index) "§d[§a+§d]" else "§7[§a+§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-add").colored())
                .clickRunCommand("/shiningapi editor chat mode ADD.$index")
                .append(" ")
                .append("§7[§c-§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-remove").colored())
                .clickRunCommand("/shiningapi editor chat mode REMOVE.$index")
            
            if (index > 0) {
                component.append(" ")
                    .append("§7[§e↑§7]")
                    .hoverText(player.getLangText("text-editor-chat-session-text_list-up").colored())
                    .clickRunCommand("/shiningapi editor chat mode UP.$index")
            }
            if (index < content.size - 1) {
                component.append(" ")
                    .append("§7[§e↓§7]")
                    .hoverText(player.getLangText("text-editor-chat-session-text_list-down").colored())
                    .clickRunCommand("/shiningapi editor chat mode DOWN.$index")
            }
            
            component.newLine()
        }

        component.append(if (mode == ADD && index == content.size) "§7| §d[§a+§d]" else "§7| [§a+§7]")
            .hoverText(player.getLangText("text-editor-chat-session-text_list-add").colored())
            .clickRunCommand("/shiningapi editor chat mode ADD.${content.size}")
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

            else -> {}
        }

        isCorrect = content.isNotEmpty()
    }

    override fun mode(player: Player, mode: String) {
        val split = mode.split('.')
        if (split.size < 2) return

        val theMode = Mode.fromString(split[0]) ?: return
        val theIndex = split[1].toIntOrNull() ?: return

        when (theMode) {
            REMOVE -> {
                if (theIndex in content.indices) {
                    content.removeAt(theIndex)
                } else {
                    player.sendPrefixedLangText("text-editor-chat-session-text_list-index_out_of_bounds")
                    return
                }
            }
            
            UP -> {
                if (theIndex in 1..content.lastIndex) {
                    val theText = content.removeAt(theIndex)
                    content.add(theIndex - 1, theText)
                } else {
                    player.sendPrefixedLangText("text-editor-chat-session-text_list-index_out_of_bounds")
                    return
                }
            }
            
            DOWN -> {
                if (theIndex in 0 until content.lastIndex) {
                    val theText = content.removeAt(theIndex)
                    content.add(theIndex + 1, theText)
                } else {
                    player.sendPrefixedLangText("text-editor-chat-session-text_list-index_out_of_bounds")
                    return
                }
            }
            
            else -> {
                this.mode = theMode
            }
        }
        
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
        REMOVE,
        UP,
        DOWN;

        companion object {
            fun fromString(mode: String): Mode? {
                return when (mode.uppercase()) {
                    "EDIT" -> EDIT
                    "ADD" -> ADD
                    "REMOVE" -> REMOVE
                    "UP" -> UP
                    "DOWN" -> DOWN
                    else -> null
                }
            }
        }
    }

}
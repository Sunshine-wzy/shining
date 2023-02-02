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

open class TextList(name: String) : ChatEditorSession(name) {
    private val list: MutableList<String> = LinkedList()
    var submitCallback: (list: MutableList<String>) -> Unit = {}
        private set
    var cancelCallback: (list: MutableList<String>) -> Unit = {}
        private set
    var finalCallback: (list: MutableList<String>) -> Unit = {}
        private set
    var mode: Mode = ADD
        private set
    var index: Int = 0
        private set
    
    
    override fun display(player: Player, json: TellrawJson) {
        list.forEachIndexed { index, str -> 
            json.append(if(mode == EDIT && index == this.index) "§7| §d${index + 1}. §f" else "§7| ${index + 1}. §f")
                .append(str.colored())
                .hoverText(player.getLangText("text-editor-chat-session-text_list-edit").colored())
                .runCommand("/shiningapi editor chat mode EDIT.$index")
                .append("    ")
                .append("§7[§b#§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-input").colored())
                .suggestCommand(str)
                .append(" ")
                .append(if(mode == ADD && index == this.index) "§d[§a+§d]" else "§7[§a+§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-add").colored())
                .runCommand("/shiningapi editor chat mode ADD.$index")
                .append(" ")
                .append("§7[§c-§7]")
                .hoverText(player.getLangText("text-editor-chat-session-text_list-remove").colored())
                .runCommand("/shiningapi editor chat mode REMOVE.$index")
                .newLine()
        }
        
        json.append(if(mode == ADD && this.index == list.size) "§7| §d[§a+§d]" else "§7| [§a+§7]")
            .hoverText(player.getLangText("text-editor-chat-session-text_list-add").colored())
            .runCommand("/shiningapi editor chat mode ADD.${list.size}")
    }

    override fun submit(player: Player) {
        submitCallback(list)
    }

    override fun cancel(player: Player) {
        cancelCallback(list)
    }

    override fun final(player: Player) {
        finalCallback(list)
    }

    override fun update(event: AsyncPlayerChatEvent) {
        when(mode) {
            EDIT -> {
                if(index in list.indices) {
                    list[index] = event.message
                } else {
                    event.player.sendPrefixedLangText("text-editor-chat-session-text_list-index_out_of_bounds")
                }
            }
            
            ADD -> {
                if(index in list.indices) {
                    list.add(index, event.message)
                    index++
                } else {
                    list.add(event.message)
                    index = list.size
                }
            }
            
            REMOVE -> {}
        }
    }

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun mode(player: Player, mode: String) {
        val split = mode.split('.')
        if(split.size < 2) return
        
        val theMode = Mode.fromString(split[0]) ?: return
        val theIndex = split[1].toIntOrNull() ?: return

        if(theMode == REMOVE) {
            if(theIndex in list.indices) {
                list.removeAt(theIndex)
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
    

    fun onSubmit(block: (list: MutableList<String>) -> Unit) {
        submitCallback = block
    }

    fun onCancel(block: (list: MutableList<String>) -> Unit) {
        cancelCallback = block
    }

    fun onFinal(block: (list: MutableList<String>) -> Unit) {
        finalCallback = block
    }
    
    fun list(list: List<String>) {
        if(list.isEmpty()) return
        
        this.list += list
        this.index = this.list.size
        this.isCorrect = true
    }
    
    fun getList(): List<String> = list
    
    
    enum class Mode {
        EDIT,
        ADD,
        REMOVE;
        
        companion object {
            fun fromString(mode: String): Mode? {
                return when(mode.uppercase()) {
                    "EDIT" -> EDIT
                    "ADD" -> ADD
                    "REMOVE" -> REMOVE
                    else -> null
                }
            }
        }
    }
    
}
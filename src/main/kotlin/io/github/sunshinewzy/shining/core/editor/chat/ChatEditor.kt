package io.github.sunshinewzy.shining.core.editor.chat

import io.github.sunshinewzy.shining.core.lang.sendLangText
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@SkipTo(LifeCycle.ENABLE)
object ChatEditor {
    private val sessionMap: ConcurrentHashMap<UUID, ChatEditorSession> = ConcurrentHashMap()
    
    fun open(
        player: Player,
        name: String = "",
        isInvisible: Boolean = true,
        predicate: AsyncPlayerChatEvent.(String) -> Boolean = { true },
        callback: (content: String) -> Unit
    ) {
        val session = ChatEditorSession(player, name, isInvisible, predicate, callback)
        sessionMap[player.uniqueId] = session
        session.send(player)
    }
    
    fun submit(player: Player) {
        sessionMap[player.uniqueId]?.let { session ->
            if(session.isCorrect) {
                sessionMap.remove(player.uniqueId)
                player.sendLangText("text-editor-chat-session-submit_correct", session.name)
                session.callback(session.content)
            } else {
                player.sendLangText("text-editor-chat-session-submit_incorrect", session.name)
            }
        }
    }
    
    fun cancel(player: Player) {
        sessionMap.remove(player.uniqueId)?.let { session ->
            player.sendLangText("text-editor-chat-session-cancel", session.name)
        }
    }
    
    @SubscribeEvent(EventPriority.LOWEST)
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        if(event.message.isEmpty()) return
        
        sessionMap[event.player.uniqueId]?.let { session->
            if(session.isInvisible) event.isCancelled = true
            
            val content = event.message
            if(session.action(event, content)) {
                session.content = content
                session.isCorrect = true
                session.send(event.player)
            } else {
                if(session.content.isEmpty()) {
                    session.content = content
                    session.send(event.player)
                }
                
                event.player.sendLangText("text-editor-chat-session-incorrect")
            }
        }
    }
    
}
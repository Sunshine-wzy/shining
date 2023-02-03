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
    private val sessionMap: ConcurrentHashMap<UUID, ChatEditorSession<*>> = ConcurrentHashMap()
    
    fun open(player: Player, session: ChatEditorSession<*>) {
        sessionMap[player.uniqueId] = session
        session.send(player)
    }
    
    fun getSession(player: Player): ChatEditorSession<*>? =
        sessionMap[player.uniqueId]
    
    fun submit(player: Player) {
        getSession(player)?.let { session ->
            if(session.isCorrect) {
                sessionMap.remove(player.uniqueId)
                player.sendLangText("text-editor-chat-session-submit_correct", session.name)
                session.submit(player)
                session.final(player)
            } else {
                player.sendLangText("text-editor-chat-session-submit_incorrect", session.name)
            }
        }
    }
    
    fun cancel(player: Player) {
        sessionMap.remove(player.uniqueId)?.let { session ->
            player.sendLangText("text-editor-chat-session-cancel", session.name)
            session.cancel(player)
            session.final(player)
        }
    }
    
    @SubscribeEvent(EventPriority.LOWEST)
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        if(event.message.isEmpty()) return
        
        sessionMap[event.player.uniqueId]?.let { session->
            if(session.isInvisible) event.isCancelled = true
            
            if(session.predicate(event, event.message)) {
                session.update(event)
                session.send(event.player)
            } else {
                event.player.sendLangText("text-editor-chat-session-incorrect")
            }
        }
    }
    
}


inline fun <reified T: ChatEditorSession<*>> buildChatEditorSession(name: String = "", builder: T.() -> Unit): T =
    T::class.java.getDeclaredConstructor(String::class.java).newInstance(name).also(builder)

inline fun <reified T: ChatEditorSession<*>> Player.openChatEditor(name: String = "", closeInventory: Boolean = true, builder: T.() -> Unit) {
    ChatEditor.open(this, buildChatEditorSession(name, builder))
    if(closeInventory) closeInventory()
}
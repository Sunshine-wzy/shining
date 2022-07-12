package io.github.sunshinewzy.sunstcore.listeners

import io.github.sunshinewzy.sunstcore.utils.PlayerChatSubscriber
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import java.util.*
import kotlin.collections.HashSet

object ChatListener {
    private val playerSubscribers = HashSet<PlayerChatSubscriber>()
    
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onAsyncPlayerChat(e: AsyncPlayerChatEvent) {
        if(e.message == "") return
        
        val uuid = e.player.uniqueId
        val shouldRemoveSubscribers = LinkedList<PlayerChatSubscriber>()
        playerSubscribers.forEach { subscriber ->
            if(uuid == subscriber.uuid) {
                try {
                    if(subscriber.action(e)) {
                        shouldRemoveSubscribers += subscriber
                    }
                    e.isCancelled = true
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        
        shouldRemoveSubscribers.forEach { 
            playerSubscribers.remove(it)
        }
    }
    
    
    fun registerPlayerChatSubscriber(subscriber: PlayerChatSubscriber) {
        playerSubscribers += subscriber
    }
    
}
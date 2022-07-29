package io.github.sunshinewzy.sunstcore.listeners

import io.github.sunshinewzy.sunstcore.SunSTCore
import io.github.sunshinewzy.sunstcore.utils.PlayerChatSubscriber
import io.github.sunshinewzy.sunstcore.utils.sendMsg
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import java.util.*

object ChatListener {
    private val playerSubscribers = HashSet<PlayerChatSubscriber>()
    
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onAsyncPlayerChat(e: AsyncPlayerChatEvent) {
        if(e.message == "") return
        
        val uuid = e.player.uniqueId
        val shouldRemoveSubscribers = LinkedList<PlayerChatSubscriber>()
        playerSubscribers.forEach { subscriber ->
            if(uuid == subscriber.uuid) {
                if(subscriber.isInvisible) e.isCancelled = true
                
                if(subscriber.isDotCancel && e.message == ".") {
                    e.player.sendMsg(SunSTCore.prefixName, "&6${subscriber.description} &6已取消")
                    shouldRemoveSubscribers += subscriber
                    return@forEach
                }
                
                try {
                    if(subscriber.action(e)) {
                        shouldRemoveSubscribers += subscriber
                    }
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
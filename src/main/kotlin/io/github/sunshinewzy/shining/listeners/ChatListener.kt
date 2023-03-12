package io.github.sunshinewzy.shining.listeners

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.utils.PlayerChatSubscriber
import io.github.sunshinewzy.shining.utils.sendMsg
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ChatListener {
    private val playerChatSubscriberMap: ConcurrentHashMap<UUID, MutableList<PlayerChatSubscriber>> =
        ConcurrentHashMap()


    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        if (event.message.isEmpty()) return

        playerChatSubscriberMap[event.player.uniqueId]?.let { subscribers ->
            val subscriber = subscribers.firstOrNull() ?: return@let

            if (subscriber.isInvisible) event.isCancelled = true

            if (subscriber.isDotCancel && event.message == ".") {
                event.player.sendMsg(Shining.prefix, "&6${subscriber.description} &6已取消")
                subscribers.remove(subscriber)
            }

            if (subscriber.action(event)) {
                subscribers.remove(subscriber)
            }
        }
    }


    fun registerPlayerChatSubscriber(subscriber: PlayerChatSubscriber) {
        playerChatSubscriberMap
            .getOrPut(subscriber.uuid) { LinkedList() }
            .add(subscriber)
    }

}
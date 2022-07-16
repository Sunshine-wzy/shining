package io.github.sunshinewzy.sunstcore.utils

import io.github.sunshinewzy.sunstcore.listeners.ChatListener
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*


/**
 * @param action If it returns true, the subscriber will be removed.
 */
class PlayerChatSubscriber(
    val uuid: UUID,
    val description: String = "",
    val isDotCancel: Boolean = true,
    val action: AsyncPlayerChatEvent.() -> Boolean
) {
    
    constructor(
        player: Player,
        description: String = "",
        isDotCancel: Boolean = true,
        action: AsyncPlayerChatEvent.() -> Boolean
    ) : this(player.uniqueId, description, isDotCancel, action)
    
    
    fun register() {
        ChatListener.registerPlayerChatSubscriber(this)
    }
    
}
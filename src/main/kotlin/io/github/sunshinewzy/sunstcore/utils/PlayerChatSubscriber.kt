package io.github.sunshinewzy.sunstcore.utils

import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*


/**
 * @param action If it returns true, the subscriber will be removed.
 */
class PlayerChatSubscriber(val uuid: UUID, val action: AsyncPlayerChatEvent.() -> Boolean) {
    
    constructor(player: Player, action: AsyncPlayerChatEvent.() -> Boolean) : this(player.uniqueId, action)
    
}
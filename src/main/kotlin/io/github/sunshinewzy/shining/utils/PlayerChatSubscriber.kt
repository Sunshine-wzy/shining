package io.github.sunshinewzy.shining.utils

import io.github.sunshinewzy.shining.listeners.ChatListener
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*


/**
 * When a player sends a message and the message is not empty, the [action] will be executed.
 *
 * WARNING: The [action] will be executed asynchronously. Make sure the [action] is thread-safe.
 *
 * @param description If [isDotCancel] is true and the player sends ".", it will be sent to the player and the subscriber will be removed.
 * @param isDotCancel If it is true and the player sends ".", the [description] will be sent to the player and the subscriber will be removed.
 * @param isInvisible If it is true, the message which the player sends will be invisible.
 * @param action If it returns true, the subscriber will be removed.
 */
class PlayerChatSubscriber(
    val uuid: UUID,
    val description: String = "",
    val isDotCancel: Boolean = true,
    val isInvisible: Boolean = true,
    val action: AsyncPlayerChatEvent.() -> Boolean
) {

    constructor(
        player: Player,
        description: String = "",
        isDotCancel: Boolean = true,
        isInvisible: Boolean = true,
        action: AsyncPlayerChatEvent.() -> Boolean
    ) : this(player.uniqueId, description, isDotCancel, isInvisible, action)


    fun register() {
        ChatListener.registerPlayerChatSubscriber(this)
    }

}
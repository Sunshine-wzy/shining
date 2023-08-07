package io.github.sunshinewzy.shining.api.addon

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import taboolib.platform.type.BukkitProxyEvent
import java.io.Closeable

interface ShiningAddonManager {
    
    val addon: ShiningAddon
    
    
    fun <T: Event> registerListener(
        event: Class<T>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        func: Closeable.(T) -> Unit
    ): BukkitProxyEvent
    
}
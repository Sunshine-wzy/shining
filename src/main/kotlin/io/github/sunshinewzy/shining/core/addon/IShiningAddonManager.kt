package io.github.sunshinewzy.shining.core.addon

import io.github.sunshinewzy.shining.api.event.ShiningListener
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.io.Closeable

interface IShiningAddonManager {
    
    fun getAddon(): ShiningAddon
    
    
    fun <T: Event> registerListener(
        event: Class<T>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        func: Closeable.(T) -> Unit
    ): ShiningListener
    
    fun registerListener(listener: Listener): Listener
    
    fun unregisterListener(listener: Listener)
    
    fun unregisterListeners()

}


inline fun <reified T: Event> IShiningAddonManager.registerListener(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = true,
    noinline func: Closeable.(T) -> Unit
): ShiningListener = registerListener(T::class.java, priority, ignoreCancelled, func)
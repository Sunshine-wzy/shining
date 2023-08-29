package io.github.sunshinewzy.shining.core.addon

import io.github.sunshinewzy.shining.Shining
import io.github.sunshinewzy.shining.api.addon.IShiningAddonManager
import io.github.sunshinewzy.shining.api.addon.ShiningAddon
import io.github.sunshinewzy.shining.api.event.CloseableListener
import io.github.sunshinewzy.shining.api.event.ShiningListener
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import taboolib.platform.BukkitListener
import taboolib.platform.BukkitPlugin
import java.io.Closeable
import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class ShiningAddonManager(private val addon: ShiningAddon) : IShiningAddonManager {

    private val listenEvents: MutableSet<Class<*>> = ConcurrentHashMap.newKeySet()
    private val registeredListeners: MutableList<Listener> = Collections.synchronizedList(LinkedList())
    
    
    override fun getAddon(): ShiningAddon = addon

    @Suppress("UNCHECKED_CAST")
    override fun <T : Event> registerListener(
        event: Class<T>,
        priority: EventPriority,
        ignoreCancelled: Boolean,
        func: Closeable.(T) -> Unit
    ): ShiningListener {
        listenEvents += event
        val closeableListener = CloseableListener(this)
        val listener = ShiningListener(BukkitListener.BukkitListener(event) { 
            BukkitPlugin.getIsolatedClassLoader()?.runIsolated { func(closeableListener, it as T) }
        })
        closeableListener.listener = listener
        Shining.pluginManager.registerEvent(event, listener, priority, listener, Shining.plugin, ignoreCancelled)
        registeredListeners += listener
        return listener
    }

    override fun registerListener(listener: Listener): Listener {
        Shining.pluginManager.registerEvents(listener, Shining.plugin)
        registeredListeners += listener
        return listener
    }

    override fun unregisterListener(listener: Listener) {
        registeredListeners -= listener
        HandlerList.unregisterAll(listener)
    }

    override fun unregisterListeners() {
        val list = registeredListeners.toList()
        registeredListeners.clear()
        list.forEach {
            HandlerList.unregisterAll(it)
        }
    }
    
}

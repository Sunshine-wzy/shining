package io.github.sunshinewzy.shining.core.event

import io.github.sunshinewzy.shining.core.addon.IShiningAddonManager
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import taboolib.common.platform.event.ProxyListener
import taboolib.platform.BukkitListener
import java.io.Closeable

class ShiningListener(
    val bukkitListener: BukkitListener.BukkitListener
) : Listener by bukkitListener, EventExecutor by bukkitListener, ProxyListener by bukkitListener


class CloseableListener(val addonManager: IShiningAddonManager) : Closeable {

    var listener: Listener? = null

    override fun close() {
        addonManager.unregisterListener(listener ?: error("close untimely"))
    }
    
}
package io.github.sunshinewzy.shining.api.event

import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import taboolib.common.platform.event.ProxyListener
import taboolib.common.platform.function.unregisterListener
import taboolib.platform.BukkitListener
import java.io.Closeable

class ShiningListener(
    val bukkitListener: BukkitListener.BukkitListener
) : Listener by bukkitListener, EventExecutor by bukkitListener, ProxyListener by bukkitListener


class CloseableListener : Closeable {

    var proxyListener: ProxyListener? = null

    override fun close() {
        unregisterListener(proxyListener ?: error("close untimely"))
    }
    
}
package io.github.sunshinewzy.sunstcore.core.data.container

import io.github.sunshinewzy.sunstcore.api.data.ISerialDataRoot
import io.github.sunshinewzy.sunstcore.api.data.container.ISerialDataContainer
import io.github.sunshinewzy.sunstcore.api.namespace.NamespacedId
import io.github.sunshinewzy.sunstcore.core.data.SerialDataRoot
import java.util.concurrent.ConcurrentHashMap

class SerialDataContainer : ISerialDataContainer {
    private val map = ConcurrentHashMap<NamespacedId, ISerialDataRoot>()


    override fun get(key: NamespacedId): ISerialDataRoot {
        map[key]?.let { return it }

        return SerialDataRoot(key.id, this).also { map[key] = it }
    }
    
}